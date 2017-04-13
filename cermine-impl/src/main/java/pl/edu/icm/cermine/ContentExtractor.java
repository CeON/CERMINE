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
package pl.edu.icm.cermine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import com.google.common.collect.Lists;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.jdom.DocType;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.configuration.ExtractionConfigRegister;
import pl.edu.icm.cermine.configuration.ExtractionConfigBuilder;
import pl.edu.icm.cermine.configuration.ExtractionConfigProperty;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxImage;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.tools.timeout.Timeout;
import pl.edu.icm.cermine.tools.timeout.TimeoutException;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister; 

/**
 * Extracts content from PDF files.
 * <p>
 * It stores the results of the extraction in various formats. The extraction
 * process is performed only if the requested result is not available yet.
 * <p>
 * User can set a timeout to limit the processing time of the <code>get*</code>
 * methods of the class.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @author Mateusz Kobos
 */
public class ContentExtractor {

    private static final long SECONDS_TO_MILLIS = 1000;

    private final InternalContentExtractor extractor;

    private Timeout mainTimeout = new Timeout();

    private static int progressCount = 0;

    /**
     * Creates the object with overridden default configuration.
     *
     * @throws AnalysisException thrown when there was an error while initializing object
     */
    public ContentExtractor() throws AnalysisException {
        this.extractor = new InternalContentExtractor();
    }
    
    /**
     * Creates the object with overridden default configuration and sets 
     * the object-bound timeout before any other initialization in the constructor is done.
     * <p>See {@link #setTimeout(long)} for more details about the timeout.</p>
     * 
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException thrown when there was an error while initializing object
     * @throws TimeoutException thrown when timeout deadline has passed.
     */
    public ContentExtractor(long timeoutSeconds) throws AnalysisException, TimeoutException {
        this.setTimeout(timeoutSeconds);
        try {
            TimeoutRegister.set(mainTimeout);
            TimeoutRegister.get().check();
            this.extractor = new InternalContentExtractor();
        } finally {
            TimeoutRegister.remove();
        }
    }


    /**
     * Set object-bound timeout.
     * <p>
     * If the deadline time specified by the timeout passes while one of the
     * <code>get*</code> methods of the class is running, the processing stops
     * and the method throws an exception.
     * <p>
     * In case the <code>get*</code> method defines a timeout by itself, it is
     * treated as an additional time restriction (i.e., the effective timeout
     * deadline is a minimum value of both timeout deadlines).
     * <p>
     * The value of the timeout is approximate. This is because in some cases,
     * the program might be allowed to slightly exceeded the timeout, say by a
     * second or two (depending on the processor speed and processed file
     * complexity).
     *
     * @param timeoutSeconds approximate timeout in seconds
     */
    public void setTimeout(long timeoutSeconds) {
        this.mainTimeout = new Timeout(timeoutSeconds * SECONDS_TO_MILLIS);
    }

    /**
     * Remove the object-bound timeout.
     */
    public void removeTimeout() {
        this.mainTimeout = new Timeout();
    }

    /**
     * Stores the input PDF stream.
     *
     * @param pdfFile PDF stream
     * @throws IOException IOException
     */
    public void setPDF(InputStream pdfFile) throws IOException {
        this.extractor.setPDF(pdfFile);
    }
    
    /**
     * Sets the input bx document.
     *
     * @param bxDocument bx document
     * @throws IOException IOException
     */
    public void setBxDocument(BxDocument bxDocument) throws IOException {
        this.extractor.setBxDocument(bxDocument);
    }

    /**
     * Resets the extraction results.
     *
     * @throws IOException IOException
     */
    public void reset() throws IOException {
        this.extractor.reset();
    }

    public ComponentConfiguration getConf() {
        return this.extractor.getConf();
    }

    public void setConf(ComponentConfiguration conf) {
        this.extractor.setConf(conf);
    }

    private BxDocument getBxDocument(Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getBxDocument();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts geometric structure.
     *
     * @return geometric structure
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public BxDocument getBxDocument()
            throws AnalysisException, TimeoutException {
        return getBxDocument(mainTimeout);
    }

    /**
     * The same as {@link #getBxDocument()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return geometric structure
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public BxDocument getBxDocument(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getBxDocument(combineWithMainTimeout(timeoutSeconds));
    }
    
    private BxDocument getBxDocumentWithGeneralLabels(Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getBxDocumentWithGeneralLabels();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts geometric structure with general labels.
     *
     * @return geometric structure
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public BxDocument getBxDocumentWithGeneralLabels()
            throws AnalysisException, TimeoutException {
        return getBxDocumentWithGeneralLabels(mainTimeout);
    }

    /**
     * The same as {@link #getBxDocumentWithGeneralLabels()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return geometric structure
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public BxDocument getBxDocumentWithGeneralLabels(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getBxDocumentWithGeneralLabels(combineWithMainTimeout(timeoutSeconds));
    }
    
    private BxDocument getBxDocumentWithSpecificLabels(Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getBxDocumentWithSpecificLabels();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts geometric structure with specific labels.
     *
     * @return geometric structure
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public BxDocument getBxDocumentWithSpecificLabels()
            throws AnalysisException, TimeoutException {
        return getBxDocumentWithSpecificLabels(mainTimeout);
    }

    /**
     * The same as {@link #getBxDocumentWithSpecificLabels()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return geometric structure
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public BxDocument getBxDocumentWithSpecificLabels(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getBxDocumentWithSpecificLabels(combineWithMainTimeout(timeoutSeconds));
    }
    
    private List<BxImage> getImages(String imagesPrefix, Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getImages(imagesPrefix);
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts images.
     *
     * @param imagesPrefix images prefix
     * @return images
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public List<BxImage> getImages(String imagesPrefix)
            throws AnalysisException, TimeoutException {
        return getImages(imagesPrefix, mainTimeout);
    }

    /**
     * The same as {@link #getImages(String)} but with a timeout.
     *
     * @param imagesPrefix imagesPrefix
     * @param timeoutSeconds approximate timeout in seconds
     * @return images
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public List<BxImage> getImages(String imagesPrefix, long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getImages(imagesPrefix, combineWithMainTimeout(timeoutSeconds));
    }
    
    private DocumentMetadata getMetadata(Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getMetadata();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts the metadata.
     *
     * @return the metadata
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public DocumentMetadata getMetadata()
            throws AnalysisException, TimeoutException {
        return getMetadata(mainTimeout);
    }

    /**
     * The same as {@link #getMetadata()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return metadata
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public DocumentMetadata getMetadata(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getMetadata(combineWithMainTimeout(timeoutSeconds));
    }

    private Element getMetadataAsNLM(Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getMetadataAsNLM();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts the metadata in NLM format.
     *
     * @return the metadata in NLM format
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getMetadataAsNLM()
            throws AnalysisException, TimeoutException {
        return getMetadataAsNLM(mainTimeout);
    }

    /**
     * The same as {@link #getMetadataAsNLM()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return metadata in NLM
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getMetadataAsNLM(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getMetadataAsNLM(combineWithMainTimeout(timeoutSeconds));
    }

    private List<BibEntry> getReferences(Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getReferences();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts the references.
     *
     * @return the list of references
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public List<BibEntry> getReferences()
            throws AnalysisException, TimeoutException {
        return getReferences(mainTimeout);
    }

    /**
     * The same as {@link #getReferences()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return list of references
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public List<BibEntry> getReferences(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getReferences(combineWithMainTimeout(timeoutSeconds));
    }

    private List<Element> getReferencesAsNLM(Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getReferencesAsNLM();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts the references in NLM format.
     *
     * @return the list of references
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public List<Element> getReferencesAsNLM()
            throws AnalysisException, TimeoutException {
        return getReferencesAsNLM(mainTimeout);
    }

    /**
     * The same as {@link #getReferencesAsNLM()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return the list of references
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public List<Element> getReferencesAsNLM(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getReferencesAsNLM(combineWithMainTimeout(timeoutSeconds));
    }

    private String getRawFullText(Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getRawFullText();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts raw text.
     *
     * @return raw text
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public String getRawFullText()
            throws AnalysisException, TimeoutException {
        return getRawFullText(mainTimeout);
    }

    /**
     * The same as {@link #getRawFullText()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return full text
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public String getRawFullText(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getRawFullText(combineWithMainTimeout(timeoutSeconds));
    }

    private Element getLabelledFullText(Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getLabelledFullText();
        } finally {
            TimeoutRegister.remove();
        }
    }
    /**
     * Extracts labeled raw text.
     *
     * @return labeled raw text
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getLabelledFullText()
            throws AnalysisException, TimeoutException {
        return getLabelledFullText(mainTimeout);
    }

    /**
     * The same as {@link #getLabelledFullText()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return labelled full text
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getLabelledFullText(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getLabelledFullText(combineWithMainTimeout(timeoutSeconds));
    }
    
    private ContentStructure getBody(Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getBody();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts structured full text.
     *
     * @return full text
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public ContentStructure getBody()
            throws AnalysisException, TimeoutException {
        return getBody(mainTimeout);
    }

    /**
     * The same as {@link #getBody()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return full text
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public ContentStructure getBody(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getBody(combineWithMainTimeout(timeoutSeconds));
    }
    
    private Element getBodyAsNLM(String imagesPrefix, Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getBodyAsNLM(imagesPrefix);
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts structured full text.
     *
     * @return full text in NLM format
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getBodyAsNLM()
            throws AnalysisException, TimeoutException {
        return getBodyAsNLM(null, mainTimeout);
    } 
    
    /**
     * Extracts structured full text.
     *
     * @param imagesPrefix images prefix
     * @return full text in NLM format
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getBodyAsNLM(String imagesPrefix)
            throws AnalysisException, TimeoutException {
        return getBodyAsNLM(imagesPrefix, mainTimeout);
    }

    /**
     * The same as {@link #getBodyAsNLM()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return full text in NLM
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getBodyAsNLM(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getBodyAsNLM(null, combineWithMainTimeout(timeoutSeconds));
    }
    
    /**
     * The same as {@link #getBodyAsNLM()} but with a timeout.
     *
     * @param imagesPrefix images prefix
     * @param timeoutSeconds approximate timeout in seconds
     * @return full text in NLM
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getBodyAsNLM(String imagesPrefix, long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getBodyAsNLM(imagesPrefix, combineWithMainTimeout(timeoutSeconds));
    }
    
    private Element getContentAsNLM(String imagesPrefix, Timeout timeout)
            throws AnalysisException, TimeoutException {
        try {
            TimeoutRegister.set(timeout);
            TimeoutRegister.get().check();
            return extractor.getContentAsNLM(imagesPrefix);
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts full content in NLM format.
     *
     * @return full content in NLM format
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getContentAsNLM()
            throws AnalysisException, TimeoutException {
        return getContentAsNLM(null, mainTimeout);
    }
    
    /**
     * Extracts full content in NLM format.
     *
     * @param imagesPrefix imagesPrefix
     * @return full content in NLM format
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getContentAsNLM(String imagesPrefix)
            throws AnalysisException, TimeoutException {
        return getContentAsNLM(imagesPrefix, mainTimeout);
    }

    /**
     * The same as {@link #getContentAsNLM()} but with a timeout.
     *
     * @param timeoutSeconds approximate timeout in seconds
     * @return the content in NLM
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getContentAsNLM(long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getContentAsNLM(null, combineWithMainTimeout(timeoutSeconds));
    }
    
    /**
     * The same as {@link #getContentAsNLM()} but with a timeout.
     *
     * @param imagesPrefix images prefix
     * @param timeoutSeconds approximate timeout in seconds
     * @return the content in NLM
     * @throws AnalysisException AnalysisException
     * @throws TimeoutException thrown when timeout deadline has passed. See
     * {@link #setTimeout(long)} for additional information about the timeout.
     */
    public Element getContentAsNLM(String imagesPrefix, long timeoutSeconds)
            throws AnalysisException, TimeoutException {
        return getContentAsNLM(imagesPrefix, combineWithMainTimeout(timeoutSeconds));
    }
    
    private Timeout combineWithMainTimeout(long timeoutSeconds) {
        Timeout local = new Timeout(timeoutSeconds * SECONDS_TO_MILLIS);
        return Timeout.min(mainTimeout, local);
    }


    synchronized void printProgress(int total) {
        progressCount++;
        int percentage = progressCount * 100 / total;
        System.out.println("Progress: " + percentage + "% done (" + progressCount + " out of " + total + ")");
        System.out.println("");
    }

    private static class parallelTask implements Runnable {
        File file;
        Map<String, String> extensions;
        boolean override;
        Long timeoutSeconds;
        String outpath;
        Long total;

         parallelTask ( File files, Map<String, String> extensions, boolean override, Long timeoutSeconds, String outpath, Long limit) { 
            this.file = files;
            this.extensions = extensions;
            this.override = override;
            this.timeoutSeconds = timeoutSeconds;
            this.outpath = outpath;
            this.total = limit;
        }

        public void run() {
            try {
                runParallel();
            }catch(IOException e) {
                printException(e);
            }catch(ParseException e) {
                 printException(e);
            }catch(AnalysisException e) {
                printException(e);
            }catch(TransformationException e) {
                printException(e);
            } 
            
        }

        public void runParallel () throws ParseException, AnalysisException, IOException, TransformationException {
            Map<String, File> outputs = new HashMap<String, File>();
            for (Map.Entry<String, String> entry : extensions.entrySet()) {
                File outputFile = getOutputFile(outpath, file, entry.getValue());
                if (override || !outputFile.exists()) {
                    outputs.put(entry.getKey(), outputFile);
                }
            }
            if (outputs.isEmpty()) {
                return;
            }
          

            ContentExtractor extractor = null;
            try {
                extractor = createContentExtractor(timeoutSeconds);
          
                InputStream in = new FileInputStream(file);
                extractor.setPDF(in);
                
                if (outputs.containsKey("images")) {
                    List<BxImage> images = extractor.getImages(outputs.get("images").getPath());
                    FileUtils.forceMkdir(outputs.get("images"));
                    for (BxImage image : images) {
                        ImageIO.write(image.getImage(), "png", new File(image.getPath()));
                    }
                }
                
                if (outputs.containsKey("jats")) {
                    Element jats;
                    if (outputs.containsKey("images")) {
                        jats = extractor.getContentAsNLM(outputs.get("images").getPath());
                    } else {
                        jats = extractor.getContentAsNLM(null);
                    }
                    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                    DocType dt = new DocType("article", "-//NLM//DTD JATS (Z39.96) Journal Archiving and Interchange DTD v1.0 20120330//EN", "JATS-archivearticle1.dtd");
                    FileUtils.writeStringToFile(outputs.get("jats"), outputter.outputString(dt), "UTF-8");
                    FileUtils.writeStringToFile(outputs.get("jats"), "\n", "UTF-8", true);
                    FileUtils.writeStringToFile(outputs.get("jats"), outputter.outputString(jats), "UTF-8", true);
                }
                
                if (outputs.containsKey("trueviz")) {
                    BxDocument doc = extractor.getBxDocumentWithSpecificLabels();
                    BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
                    Writer fw = new OutputStreamWriter(new FileOutputStream(outputs.get("trueviz")), "UTF-8");
                    writer.write(fw, Lists.newArrayList(doc), "UTF-8");
                }
                
                if (outputs.containsKey("zones")) {
                    Element text = extractor.getLabelledFullText();
                    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                    FileUtils.writeStringToFile(outputs.get("zones"), outputter.outputString(text), "UTF-8");
                }
                
                if (outputs.containsKey("text")) {
                    String text = extractor.getRawFullText();
                    FileUtils.writeStringToFile(outputs.get("text"), text, "UTF-8");
                }
                
            } catch (AnalysisException ex) {
                printException(ex);
            } catch (TransformationException ex) {
                printException(ex);
            } catch (TimeoutException ex) {
                printException(ex);
            }catch (IOException ex) {
                printException(ex);
            }catch (Exception ex) {
                printException(ex);
            } finally {
                if (extractor != null) {
                    extractor.printProgress(total.intValue());
                    extractor.removeTimeout();
                }
            }
        }
    }

    public static void main(String[] args) throws ParseException, AnalysisException, URISyntaxException, IOException, TransformationException {
        CommandLineOptionsParser parser = new CommandLineOptionsParser();
        String error = parser.parse(args);
        if (error != null) {
            System.err.println(error + "\n");
            System.err.println(
                    "Usage: ContentExtractor -path <path> [optional parameters]\n\n"
                    + "Tool for extracting metadata and content from PDF files.\n\n"
                    + "Arguments:\n"
                    + "  -path <path>           path to a directory containing PDF files\n"
                    + "  -limit <int>            (optional) the number of PDF files to parse starting from the top  default: \"all\"\n"
                    + "  -workers <int>         (optional) how many workers to run in parallel  default: \"1\"\n"
                    + "  -outpath <path>        (optional) path to a directory to write the resulting files  default: \"-path\"\n"
                    + "  -outputs <list>        (optional) comma-separated list of extraction\n"
                    + "                         output(s); possible values: \"jats\" (document\n"
                    + "                         metadata and content in NLM JATS format), \"text\"\n"
                    + "                         (raw document text), \"zones\" (text zones with\n"
                    + "                         their labels), \"trueviz\" (geometric structure in\n"
                    + "                         TrueViz format), \"images\" (images from the\n"
                    + "                         document); default: \"jats\"\n"
                    + "  -exts <list>           (optional) comma-separated list of extensions of the\n"
                    + "                         resulting files; the list has to have the same\n"
                    + "                         length as output list; default: \"cermxml,images\"\n"
                    + "  -override              override already existing files\n"
                    + "  -timeout <seconds>     (optional) approximate maximum allowed processing\n"
                    + "                         time for a PDF file in seconds; by default, no\n"
                    + "                         timeout is used; the value is approximate because in\n"
                    + "                         some cases, the program might be allowed to slightly\n"
                    + "                         exceeded this time, say by a second or two\n"
                    + "  -configuration <path>	(optional) path to configuration properties file\n"
                    + "                         see https://github.com/CeON/CERMINE\n"
                    + "                         for description of available configuration properties\n"
                    );
            System.exit(1);
        }

        final boolean override = parser.override();
        final Long timeoutSeconds = parser.getTimeout();
        
        String path = parser.getPath();
        final String outpath = parser.getOutPath()+'/';
        final Map<String, String> extensions = parser.getTypesAndExtensions();

        File file = new File(path);
        Collection<File> files = FileUtils.listFiles(file, new String[]{"pdf"}, true);

        Long getlimit = parser.getLimit();
        Long workers = parser.getWorkers();

        if (getlimit == null || getlimit > files.size()) {
            getlimit = new Long(files.size());
        }

        if (workers > getlimit) {
            workers = getlimit;
        }

        ExtractionConfigBuilder builder = new ExtractionConfigBuilder();
        if (parser.getConfigurationPath() != null) {
            builder.addConfiguration(parser.getConfigurationPath());
        }

        final Long limit = getlimit;
        builder.setProperty(ExtractionConfigProperty.IMAGES_EXTRACTION, extensions.containsKey("images"));
        ExtractionConfigRegister.set(builder.buildConfiguration());
        final ExecutorService executor = Executors.newFixedThreadPool(workers.intValue());

        final Path startFilePath = Paths.get(new URI("file://"+path));

        long start = System.currentTimeMillis();
        float elapsed;

        Files.walkFileTree(startFilePath, new SimpleFileVisitor<Path>() {
            int counter = 0;
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().toLowerCase().endsWith(".pdf") && counter < limit) {
                    Runnable worker = new parallelTask(file.toFile(), extensions, override, timeoutSeconds, outpath, limit);
                    executor.execute(worker);
                    counter++;
                } else if (counter >= limit) {
                    return FileVisitResult.SKIP_SIBLINGS;
                }
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                if (e == null) {
                    if (dir == startFilePath) {
                        executor.shutdown();
                        while (!executor.isTerminated()) {   }
                    }
                    return FileVisitResult.CONTINUE;
                } else {
                    // directory iteration failed
                    executor.shutdown();
                    throw e;
                }
            }
        });

        long end = System.currentTimeMillis();
        elapsed = (end - start) / 1000F;
        System.out.println("Total extraction time: " + Math.round(elapsed) + "s");
        
    }

    private static ContentExtractor createContentExtractor(Long timeoutSeconds)
            throws TimeoutException, AnalysisException {
        ContentExtractor extractor;
        if (timeoutSeconds != null) {
            extractor = new ContentExtractor(timeoutSeconds);
        } else {
            extractor = new ContentExtractor();
        }
        TimeoutRegister.get().check();
        return extractor;
    }

    private static File getOutputFile(String outfile, File file, String ext) {
        return new File(Paths.get(outfile+file.getName().replaceFirst("pdf$", ext)).toString());
    }
    
    private static void printException(Exception ex) {
        System.out.print("Exception occured: " + ExceptionUtils.getStackTrace(ex));
    }
}
