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

package pl.edu.icm.cermine.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import pl.edu.icm.cermine.InternalContentExtractor;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.tools.timeout.Timeout;
import pl.edu.icm.cermine.tools.timeout.TimeoutException;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * Page segmenter using Docstrum algorithm.
 * 
 * @author Krzysztof Rusek
 */
public class ParallelDocstrumSegmenter extends DocstrumSegmenter {
    
    class NumBxPage {
        int index = -1;
        BxPage page;
        List<Component> components;

        public NumBxPage(BxPage page, int index) {
            this.page = page;
            this.index = index;
        }

        public NumBxPage(BxPage page) {
            this.page = page;
        }
    }
    
    class SingleSegmenter implements Callable<NumBxPage> {
        NumBxPage page;
        private final Timeout timeout;

        public SingleSegmenter(BxPage page, int index, Timeout timeout) {
            this.page = new NumBxPage(page, index);
            this.timeout = timeout;
        }
       
        @Override
        public NumBxPage call() throws AnalysisException {
            try {
                TimeoutRegister.set(timeout);
                return new NumBxPage(segmentPage(page.page), page.index);
            } finally {
                TimeoutRegister.remove();
            }
        }
    }
    
    class ComponentCounter implements Callable<NumBxPage> {
        NumBxPage page;
        private final Timeout timeout;

        public ComponentCounter(BxPage page, Timeout timeout) {
            this.page = new NumBxPage(page);
            this.timeout = timeout;
        }
       
        @Override
        public NumBxPage call() throws AnalysisException {
            try {
                TimeoutRegister.set(timeout);
                TimeoutRegister.get().check();
                page.components = createComponents(page.page);
                return page;
            } finally {
                TimeoutRegister.remove();
            }
        }
    }
    

    @Override
    public BxDocument segmentDocument(BxDocument document) throws AnalysisException {
        Map<BxPage, List<Component>> componentMap = new HashMap<BxPage, List<Component>>();

        ExecutorService exec = Executors.newFixedThreadPool(
                InternalContentExtractor.THREADS_NUMBER);
        ArrayList<Callable<NumBxPage>> tasks = new ArrayList<Callable<NumBxPage>>();
        for (BxPage page : document) {
           tasks.add(new ComponentCounter(page, TimeoutRegister.get()));
        }
        
        List<Future<NumBxPage>> results;
        try {
            results = exec.invokeAll(tasks);
            exec.shutdown();
            
            for (Future<NumBxPage> result : results) {
                NumBxPage p = result.get();
                componentMap.put(p.page, p.components);
            }
            TimeoutRegister.get().check();
        } catch (ExecutionException ex) {
            if (ex.getCause() instanceof TimeoutException){
                throw new TimeoutException((Exception)ex);
            } else {
                throw new AnalysisException("Cannot segment pages!", ex);
            }
        } catch (InterruptedException ex) {
            throw new AnalysisException("Cannot segment pages!", ex);
        }
                
        this.computeDocumentOrientation(componentMap);
    
        BxDocument output = new BxDocument();
        BxPage[] pages = new BxPage[document.childrenCount()];
        
        exec = Executors.newFixedThreadPool(
                InternalContentExtractor.THREADS_NUMBER);
        tasks = new ArrayList<Callable<NumBxPage>>();
        int i = 0;
        for (BxPage page : document) {
           tasks.add(new SingleSegmenter(page, i++, TimeoutRegister.get()));
        }
        
        try {
            results = exec.invokeAll(tasks);
            exec.shutdown();
            
            for (Future<NumBxPage> result : results) {
                NumBxPage p = result.get();
                pages[p.index] = p.page;
            }
            TimeoutRegister.get().check();
            for (BxPage p : pages) {
                if (p.getBounds() != null) {
                    output.addPage(p);
                }
            }
            return output;
        } catch (ExecutionException ex) {
            if (ex.getCause() instanceof TimeoutException){
                throw new TimeoutException((Exception)ex);
            } else {
                throw new AnalysisException("Cannot segment pages!", ex);
            }
        } catch (InterruptedException ex) {
            throw new AnalysisException("Cannot segment pages!", ex);
        }
    }

}
