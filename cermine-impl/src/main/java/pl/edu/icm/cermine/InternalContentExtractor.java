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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;

import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationPosition;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationSentiment;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.metadata.transformers.MetadataToNLMConverter;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Content extractor from PDF files.
 * The extractor stores the results of the extraction in various formats.
 * The extraction process is performed only if the requested results 
 * is not available yet.
 * 
 * This class is intended to be used internally by the library.
 *
 * @author Dominika Tkaczyk
 */
public class InternalContentExtractor {

    public static int THREADS_NUMBER = 3;
    
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
    
    
    public InternalContentExtractor() throws AnalysisException {
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
        bxDocument = ExtractionUtils.extractStructure(conf, pdfFile);
        return bxDocument;
    }
    
    /**
     * Extracts the metadata.
     * 
     * @return the metadata
     * @throws AnalysisException 
     */
    public DocumentMetadata getMetadata() throws AnalysisException {
        if (metadata == null) {
            getBxDocument();
            metadata = ExtractionUtils.extractMetadata(conf, bxDocument);
        }
        return metadata;
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
                getMetadata();
                MetadataToNLMConverter converter = new MetadataToNLMConverter();
                nlmMetadata = converter.convert(metadata);
            }
            return nlmMetadata;
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract metadata!", ex);
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
    
    /**
     * Extracts full content in NLM format.
     * 
     * @return full content in NLM format
     * @throws AnalysisException 
     */
    public Element getNLMContent() throws AnalysisException {
        if (nlmContent == null) {
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
}
