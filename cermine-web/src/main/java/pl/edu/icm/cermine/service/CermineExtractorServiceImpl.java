/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import javax.annotation.PostConstruct;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.cermine.ContentExtractor;
import pl.edu.icm.cermine.content.transformers.NLMToHTMLWriter;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * @author Aleksander Nowinski (a.nowinski@icm.edu.pl)
 */
@Component
public class CermineExtractorServiceImpl implements CermineExtractorService {

    int threadPoolSize = 4;
    int maxQueueForBatch = 0;
    Logger log = LoggerFactory.getLogger(CermineExtractorServiceImpl.class);
    List<ContentExtractor> extractors;
    ExecutorService processingExecutor;
    ExecutorService batchProcessingExecutor;
    @Autowired
    TaskManager taskManager;

    public CermineExtractorServiceImpl() {
    }

    @PostConstruct
    public void init() {
        try {
            processingExecutor = Executors.newFixedThreadPool(threadPoolSize);
            ArrayBlockingQueue<Runnable> q;
            if (maxQueueForBatch > 0) {
                q = new ArrayBlockingQueue<Runnable>(maxQueueForBatch);
            } else {
                q = new ArrayBlockingQueue<Runnable>(100000);
            }
            batchProcessingExecutor = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 1, TimeUnit.DAYS, q);
            extractors = new ArrayList<ContentExtractor>();
            for (int i = 0; i < threadPoolSize; i++) {
                extractors.add(new ContentExtractor());
            }
        } catch (Exception ex) {
            log.error("Failed to init content extractor", ex);
            throw new RuntimeException(ex);
        }
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public int getMaxQueueForBatch() {
        return maxQueueForBatch;
    }

    public void setMaxQueueForBatch(int maxQueueForBatch) {
        this.maxQueueForBatch = maxQueueForBatch;
    }

    @Override
    public ExtractionResult extractNLM(InputStream is) throws AnalysisException, ServiceException {
        log.debug("Starting extractNLM task...");
        ExtractionResult res = new ExtractionResult();
        res.setSubmit(new Date());
        log.debug("submitting extractNLM task...");
        try {
            Future<ExtractionResult> future = batchProcessingExecutor.submit(new SimpleExtractionCallable(is, res));

            Thread.yield();
            log.debug("waiting for extractNLM task...");
            res = future.get();
        } catch (RejectedExecutionException rje) {
            throw new ServiceException("Queue size exceeded.", rje);
        } catch (Exception ex) {
            log.error("Exception while executing extraction task...", ex);
            throw new RuntimeException(ex);
        }
        log.debug("finished extractNLM task...");
        return res;
    }

    @Override
    public long initExtractionTask(byte[] pdf, String fileName) {
        ExtractionTask task = new ExtractionTask();
        task.setPdf(pdf);
        task.setFileName(fileName);
        task.setCreationDate(new Date());
        task.setStatus(ExtractionTask.TaskStatus.CREATED);
        long id = taskManager.registerTask(task);
        //now process the task...
        task.setStatus(ExtractionTask.TaskStatus.QUEUED);
        processingExecutor.submit(new ExtractingTaskExecution(task));

        return id;
    }

    protected ContentExtractor obtainExtractor() {
        log.debug("Obtaining extractor from the pool");
        ContentExtractor res = null;
        try {
            synchronized (extractors) {
                while (extractors.isEmpty()) {
                    log.debug("Extractor pool is empty, going to sleep...");
                    extractors.wait();
                }
                res = extractors.remove(0);
            }
            return res;
        } catch (InterruptedException ire) {
            log.error("Unexpected exception while waiting for extractor...", ire);
            throw new RuntimeException(ire);
        }
    }

    protected void returnExtractor(ContentExtractor e) {
        log.debug("Returning extractor to the pool...");
        synchronized (extractors) {
            try {
                e = new ContentExtractor();
                extractors.add(e);
            } catch (AnalysisException ex) {
                throw new RuntimeException("Cannot create extractor!", ex);
            }
            extractors.notify();
        }
    }

    /**
     * Method to perform real extraction.
     *
     * @param result
     * @param input
     * @return extraction results
     */
    private ExtractionResult performExtraction(ExtractionResult result, InputStream input) {
        ContentExtractor e = null;
        try {
            e = obtainExtractor();
            result.processingStart = new Date();
            log.debug("Starting extraction on the input stream...");
            e.setPDF(input);
            Element resEl = e.getContentAsNLM();
            log.debug("Extraction ok..");
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            Document doc = new Document(resEl);
            String res = outputter.outputString(doc);
            result.setNlm(res);
            String html = new NLMToHTMLWriter().write(resEl);
            result.setHtml(html);
            log.debug("Article meta extraction start:");
            result.setMeta(ArticleMeta.extractNLM(doc));
            log.debug("Article meta extraction succeeded");
            result.setSucceeded(true);
        } catch (Exception anal) {
            log.debug("Exception from analysis: ", anal);
            result.setError(anal);
            result.setSucceeded(false);
        } finally {
            if (e != null) {
                returnExtractor(e);
            }
            result.setProcessingEnd(new Date());
        }
        return result;
    }

    private class ExtractingTaskExecution implements Runnable {

        ExtractionTask task;

        public ExtractingTaskExecution(ExtractionTask task) {
            this.task = task;
        }

        @Override
        public void run() {
            log.debug("Starting processing task: " + task.getId());
            task.setStatus(ExtractionTask.TaskStatus.PROCESSING);

            ExtractionResult result = new ExtractionResult();
            result.setProcessingStart(new Date());
            result.setSubmit(task.getCreationDate());
            log.debug("Running extraction: " + task.getId());
            performExtraction(result, new ByteArrayInputStream(task.getPdf()));
            task.setResult(result);
            log.debug("Processing finished: " + task.getId());
            if (result.isSucceeded()) {
                task.setStatus(ExtractionTask.TaskStatus.FINISHED);
            } else {
                task.setStatus(ExtractionTask.TaskStatus.FAILED);
            }
            task.setPdf(null);//clean up memory, we will overflow after few request without it...
            log.debug("finishing task: " + task.getId());
        }
    }

    private class SimpleExtractionCallable implements Callable<ExtractionResult> {

        public SimpleExtractionCallable(InputStream input, ExtractionResult result) {
            this.input = input;
            this.result = result;
        }
        InputStream input;
        ExtractionResult result;

        @Override
        public ExtractionResult call() {
            return performExtraction(result, input);
        }
    }
}
