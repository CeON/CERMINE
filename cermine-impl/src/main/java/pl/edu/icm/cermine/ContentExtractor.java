/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.common.collect.Lists;

import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationPosition;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationSentiment;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.metadata.transformers.DocumentMetadataToNLMElementConverter;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.tools.timeout.StandardTimeout;
import pl.edu.icm.cermine.tools.timeout.TimeoutException;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * Content extractor from PDF files.
 * The extractor stores the results of the extraction in various formats.
 * The extraction process is performed only if the requested results 
 * is not available yet.
 *
 * @author Dominika Tkaczyk
 */
public class ContentExtractor {

    public static int THREADS_NUMBER = 3;
    
    private final long SECONDS_TO_MILLIS = 1000;
    
    private ComponentConfiguration conf;
    
    /** input PDF file */
    private InputStream pdfFile;
    
    /** document's geometric structure */
    private BxDocument bxDocument;
    
    /** document's metadata */
    private DocumentMetadata metadata;
    
    /** document's metadata in NLM format */
    private Element nlmMetadata;
    
    /** document's list of references */
    private List<BibEntry> references;
    
    /** document's list of references in NLM format */
    private List<Element> nlmReferences;
    
    /** raw full text */
    private String rawFullText;
    
    /** labelled full text format */
    private Element labelledFullText;
    
    /** structured full text in NLM format */
    private Element nlmFullText;
    
    /** extracted content in NLM format */
    private Element nlmContent;

    /** citation positions */
    private List<List<CitationPosition>> citationPositions;
    
    /** citation sentiments */
    private List<CitationSentiment> citationSentiments;
    
    
    public ContentExtractor() throws AnalysisException {
        conf = new ComponentConfiguration();
    }

    /**
     * Stores the input PDF stream.
     * 
     * @param pdfFile PDF stream
     * @throws IOException 
     */
    public void setPDF(InputStream pdfFile) throws IOException {
        reset();
        this.pdfFile = pdfFile;
    }

    /**
     * Sets the input bx document.
     * 
     * @param bxDocument 
     */
    public void setBxDocument(BxDocument bxDocument) throws IOException {
        reset();
        this.bxDocument = bxDocument;
    }
    
    /**
     * Stores citation locations.
     * 
     * @param citationPositions citation locations
     */
    public void setCitationPositions(List<List<CitationPosition>> citationPositions) {
        this.citationPositions = citationPositions;
    }

    /**
     * Stores the document's raw full text.
     * 
     * @param rawFullText raw full text
     */
    public void setRawFullText(String rawFullText) {
        this.rawFullText = rawFullText;
    }

    /**
     * Stores the document's references.
     * 
     * @param references the document's references
     */
    public void setReferences(List<BibEntry> references) {
        this.references = references;
    }
    
    /**
     * Extracts geometric structure.
     * 
     * @return geometric structure
     * @throws AnalysisException 
     */
    public BxDocument getBxDocument() throws AnalysisException {
        if (bxDocument != null) {
            return bxDocument;
        }
        if (pdfFile == null) {
            throw new AnalysisException("No PDF document uploaded!");
        }
        TimeoutRegister.get().check();
        bxDocument = ExtractionUtils.extractStructure(conf, pdfFile);
        return bxDocument;
    }
    
    /** The same as {@link #getBxDocument()} but with a timeout.
     *  
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public BxDocument getBxDocument(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getBxDocument();
        } finally {
            TimeoutRegister.remove();
        }
    }
    
    /**
     * Extracts the metadata.
     * 
     * @return the metadata
     * @throws AnalysisException 
     */
    public DocumentMetadata getMetadata() throws AnalysisException {
        if (metadata == null) {
            TimeoutRegister.get().check();
            getBxDocument();
            metadata = ExtractionUtils.extractMetadata(conf, bxDocument);
        }
        return metadata;
    }
    
    /** The same as {@link #getMetadata()} but with a timeout.
     *  
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public DocumentMetadata getMetadata(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getMetadata();
        } finally {
            TimeoutRegister.remove();
        }
    }
    
    /**
     * Extracts the metadata in NLM format.
     * 
     * @return the metadata in NLM format
     * @throws AnalysisException 
     */
    public Element getNLMMetadata() throws AnalysisException {
        try {
            if (nlmMetadata == null) {
                TimeoutRegister.get().check();
                getMetadata();
                DocumentMetadataToNLMElementConverter converter = new DocumentMetadataToNLMElementConverter();
                nlmMetadata = converter.convert(metadata);
            }
            return nlmMetadata;
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract metadata!", ex);
        }
    }
    
    /** The same as {@link #getNLMMetadata()} but with a timeout.
     *  
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public Element getNLMMetadata(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getNLMMetadata();
        } finally {
            TimeoutRegister.remove();
        }
    }
    
    /**
     * Extracts the references.
     * 
     * @return the list of references
     * @throws AnalysisException 
     */
    public List<BibEntry> getReferences() throws AnalysisException {
        if (references == null) {
            getBxDocument();
            references = Lists.newArrayList(ExtractionUtils.extractReferences(conf, bxDocument));
        }
        return references;
    }
    
    /** The same as {@link #getReferences()} but with a timeout.
     *  
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public List<BibEntry> getReferences(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getReferences();
        } finally {
            TimeoutRegister.remove();
        }
    }
    
    /**
     * Extracts the references in NLM format.
     * 
     * @return the list of references
     * @throws AnalysisException 
     */
    public List<Element> getNLMReferences() throws AnalysisException {
        if (nlmReferences == null) {
            getReferences();
            nlmReferences = Lists.newArrayList(
                ExtractionUtils.convertReferences(references.toArray(new BibEntry[]{})));
        }
        return nlmReferences;
    }
    
    /** The same as {@link #getNLMReferences()} but with a timeout.
     *  
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public List<Element> getNLMReferences(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getNLMReferences();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts the locations of the document's citations.
     * 
     * @return the locations
     * @throws AnalysisException 
     */
    public List<List<CitationPosition>> getCitationPositions() throws AnalysisException {
        if (citationPositions == null) {
            getRawFullText();
            getReferences();
            citationPositions = ExtractionUtils.findCitationPositions(conf, rawFullText, references);
        }
        return citationPositions;
    }
    
    /** The same as {@link #getCitationPositions()} but with a timeout.
     *  
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public List<List<CitationPosition>> getCitationPositions(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getCitationPositions();
        } finally {
            TimeoutRegister.remove();
        }
    }
    
    /**
     * Extractes the sentiments of the document's citations.
     * 
     * @return the citation sentiments
     * @throws AnalysisException 
     */
    public List<CitationSentiment> getCitationSentiments() throws AnalysisException {
        if (citationSentiments == null) {
            getCitationPositions();
            citationSentiments = ExtractionUtils.analyzeSentimentFromPositions(conf, rawFullText, citationPositions);
        }
        return citationSentiments;
    }
    
    /** The same as {@link #getCitationSentiments()} but with a timeout.
     *  
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public List<CitationSentiment> getCitationSentiments(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getCitationSentiments();
        } finally {
            TimeoutRegister.remove();
        }
    }
    
    /**
     * Extracts raw text.
     * 
     * @return raw text
     * @throws AnalysisException 
     */
    public String getRawFullText() throws AnalysisException {
        if (rawFullText == null) {
            getBxDocument();
            rawFullText = ExtractionUtils.extractRawText(conf, bxDocument);
        }
        return rawFullText;
    }
    
    /** The same as {@link #getRawFullText()} but with a timeout.
     *  
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public String getRawFullText(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getRawFullText();
        } finally {
            TimeoutRegister.remove();
        }
    }

    /**
     * Extracts labelled raw text.
     * 
     * @return labelled raw text
     * @throws AnalysisException 
     */
    public Element getLabelledRawFullText() throws AnalysisException {
        if (labelledFullText == null) {
            getBxDocument();
            labelledFullText = ExtractionUtils.extractRawTextWithLabels(conf, bxDocument);
        }
        return labelledFullText;
    }
    
    /** The same as {@link #getLabelledRawFullText()} but with a timeout.
     *  
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public Element getLabelledRawFullText(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getLabelledRawFullText();
        } finally {
            TimeoutRegister.remove();
        }
    }
    
    /**
     * Extracts structured full text.
     * 
     * @return full text in NLM format
     * @throws AnalysisException 
     */
    public Element getNLMText() throws AnalysisException {
        if (nlmFullText == null) {
            getBxDocument();
            getReferences();
            nlmFullText = ExtractionUtils.extractTextAsNLM(conf, bxDocument, references);
        }
        return nlmFullText;
    }
    
    /** The same as {@link #getNLMText()} but with a timeout.
     * 
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public Element getNLMText(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getNLMText();
        } finally {
            TimeoutRegister.remove();
        }
    }
    
    /**
     * Extracts full content in NLM format.
     * 
     * @return full content in NLM format
     * @throws AnalysisException 
     */
    public Element getNLMContent() throws AnalysisException {
        if (nlmContent == null) {
            TimeoutRegister.get().check();
            getNLMMetadata();
            getNLMReferences();
            getNLMText();
            
            nlmContent = new Element("article");
            
            Element meta = (Element) nlmMetadata.getChild("front").clone();
            nlmContent.addContent(meta);
            
            nlmContent.addContent(nlmFullText);
            
            Element back = new Element("back");
            Element refList = new Element("ref-list");
            for (int i = 0; i < nlmReferences.size(); i++) {
                Element ref = nlmReferences.get(i);
                Element r = new Element("ref");
                r.setAttribute("id", String.valueOf(i+1));
                r.addContent(ref);
                refList.addContent(r);
            }
            back.addContent(refList);
            nlmContent.addContent(back);
        }
        return nlmContent;
    }
    
    /** The same as {@link #getNLMContent()} but with a timeout.
     *  
     * @param timeoutSeconds approximate timeout in seconds
     * @throws AnalysisException
     */
    public Element getNLMContent(long timeoutSeconds) throws AnalysisException {
        try {
            TimeoutRegister.set(
                    new StandardTimeout(timeoutSeconds*SECONDS_TO_MILLIS));
            return getNLMContent();
        } finally {
            TimeoutRegister.remove();
        }
    }
    
    /**
     * Resets the extraction results.
     * 
     * @throws IOException 
     */
    public void reset() throws IOException {
        bxDocument = null;
        metadata = null;
        nlmMetadata = null;
        references = null;
        nlmReferences = null;
        rawFullText = null;
        nlmFullText = null;
        nlmContent = null;
        if (pdfFile != null) {
            pdfFile.close();
        }
        pdfFile = null;
    }

    public ComponentConfiguration getConf() {
        return conf;
    }

    public void setConf(ComponentConfiguration conf) {
        this.conf = conf;
    }
    
    public static void main(String[] args) throws ParseException, AnalysisException, IOException, TransformationException {
        CommandLineOptionsParser parser = new CommandLineOptionsParser();
        if (!parser.parse(args)) {
            System.err.println(
                    "Usage: ContentExtractor -path <path> [optional parameters]\n\n"
                  + "Tool for extracting metadata and content from PDF files.\n\n"
                  + "Arguments:\n"
                  + "  -path <path>              path to a PDF file or directory containing PDF files\n"
                  + "  -ext <extension>          (optional) the extension of the resulting metadata file;\n"
                  + "                            default: \"cermxml\"; used only if passed path is a directory\n"
                  + "  -modelmeta <path>         (optional) the path to the metadata classifier model file\n"
                  + "  -modelinit <path>         (optional) the path to the initial classifier model file\n"
                  + "  -str                      whether to store structure (TrueViz) files as well;\n"
                  + "                            used only if passed path is a directory\n"
                  + "  -strext <extension>       (optional) the extension of the structure (TrueViz) file;\n"
                  + "                            default: \"cxml\"; used only if passed path is a directory\n"
                  + "  -threads <num>            number of threads for parallel processing\n"
                  + "  -timeout <seconds>        approximate maximal processing time of a single document\n"
                  + "                            in seconds; if the time is exceeded, the program exits\n"
                  + "                            with an error; by default, no timeout is used.\n"
                  + "                            The value is approximate because in some cases,\n"
                  + "                            this time might be slightly exceeded,\n"
                  + "                            say by a second or two.\n");
            System.exit(1);
        }
        
        String path = parser.getPath();
        String extension = parser.getNLMExtension();
        boolean extractStr = parser.extractStructure();
        String strExtension = parser.getBxExtension();
        ContentExtractor.THREADS_NUMBER = parser.getThreadsNumber();
        Long timeoutMillis = secondsToMilliseconds(parser.getTimeout());
 
        File file = new File(path);
        if (file.isFile()) {
            try {
                if (timeoutMillis != null) {
                    TimeoutRegister.set(new StandardTimeout(timeoutMillis));
                }
                ContentExtractor extractor = new ContentExtractor();
                TimeoutRegister.get().check();
                parser.updateMetadataModel(extractor.getConf());
                parser.updateInitialModel(extractor.getConf());
                InputStream in = new FileInputStream(file);
                extractor.setPDF(in);
                TimeoutRegister.get().check();

                Element result = extractor.getNLMContent();
                
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                System.out.println(outputter.outputString(result));
            } catch (AnalysisException ex) {
                printException(ex);
            } catch (TimeoutException ex) {
                printException(ex);
            } finally {
                if (timeoutMillis != null) {
                    TimeoutRegister.remove();
                }               
            }
        } else {
        
            Collection<File> files = FileUtils.listFiles(file, new String[]{"pdf"}, true);
    
            int i = 0;
            for (File pdf : files) {
                File xmlF = new File(pdf.getPath().replaceAll("pdf$", extension));
                if (xmlF.exists()) {
                    i++;
                    continue;
                }
 
                long start = System.currentTimeMillis();
                float elapsed = 0;
                if (timeoutMillis != null) {
                    TimeoutRegister.set(new StandardTimeout(timeoutMillis));
                }
                
                System.out.println("File processed: "+pdf.getPath());
 
                try {
                    ContentExtractor extractor = new ContentExtractor();
                    parser.updateMetadataModel(extractor.getConf());
                    parser.updateInitialModel(extractor.getConf());
                    InputStream in = new FileInputStream(pdf);
                    extractor.setPDF(in);

                    BxDocument doc = extractor.getBxDocument();;
                    Element result = extractor.getNLMContent();

                    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                    if (!xmlF.createNewFile()) {
                        System.out.println("Cannot create new file!");
                    }
                    FileUtils.writeStringToFile(xmlF, outputter.outputString(result));            
                
                    if (extractStr) {
                        BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
                        File strF = new File(pdf.getPath().replaceAll("pdf$", strExtension));
                        writer.write(new FileWriter(strF), Lists.newArrayList(doc));
                    }
                } catch (AnalysisException ex) {
                    printException(ex);
                } catch (TransformationException ex) {
                    printException(ex);
                } catch (TimeoutException ex) {
                    printException(ex);
                } finally {
                    if (timeoutMillis != null) {
                        TimeoutRegister.remove();
                    }
                    long end = System.currentTimeMillis();
                    elapsed = (end - start) / 1000F;
                }
                
                i++;
                int percentage = i*100/files.size();
                System.out.println("Extraction time: " + Math.round(elapsed) + "s");
                System.out.println("Progress: "+percentage + "% done (" + i +" out of " + files.size() + ")");
                System.out.println("");
            }
        }
    }
    
    private static Long secondsToMilliseconds(Long seconds){
        final long secondsToMillis = 1000;
        Long millis = null;
        if (seconds !=  null) {
            millis = seconds * secondsToMillis;
        }
        return millis;
    }
        
    private static void printException(Exception ex) {
        System.out.print("Exception occured: " + ExceptionUtils.getStackTrace(ex));
    }
}
