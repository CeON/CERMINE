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

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.jdom.Element;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import org.jdom.Namespace;
import pl.edu.icm.cermine.ExtractionUtils.Step;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.transformers.BibEntryToNLMConverter;
import pl.edu.icm.cermine.content.RawTextWithLabelsExtractor;
import pl.edu.icm.cermine.content.citations.ContentStructureCitationPositions;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.transformers.DocContentStructToNLMElementConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.metadata.transformers.MetadataToNLMConverter;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxImage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * Content extractor from PDF files.
 * The extractor stores the results of the extraction in various formats.
 * The extraction process is performed only if the requested results 
 * is not available yet.
 * 
 * This class is intended to be used internally by the library.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class InternalContentExtractor {

    private ComponentConfiguration conf;
    
    /** input PDF file */
    private InputStream pdfFile;
    
    /** document's geometric structure */
    private BxDocument bxDocument;
    
    /** document's metadata */
    private DocumentMetadata metadata;
    
    /** document's list of references */
    private List<BibEntry> references;
    
    /** body structure */
    private ContentStructure body;
    
    
    private List<String> referenceStrings;
    
    private BxContentStructure bxBody;
    
    private ContentStructureCitationPositions citationPositions;
    
    private final Set<Step> stepsDone;
            
    
    /**
     * Creates the object with provided configuration.
     * 
     * @throws AnalysisException thrown when there was an error while initializing object
     */
    public InternalContentExtractor() throws AnalysisException {
        conf = new ComponentConfiguration();
        stepsDone = EnumSet.noneOf(Step.class);
    }

    /**
     * Stores the input PDF stream.
     * 
     * @param pdfFile PDF stream
     * @throws IOException IOException
     */
    public void setPDF(InputStream pdfFile) throws IOException {
        reset();
        this.pdfFile = pdfFile;
    }

    /**
     * Sets the input bx document.
     * 
     * @param bxDocument geometric structure
     * @throws IOException IOException
     */
    public void setBxDocument(BxDocument bxDocument) throws IOException {
        reset();
        this.bxDocument = bxDocument;
        stepsDone.add(Step.CHARACTER_EXTRACTION);
        stepsDone.add(Step.PAGE_SEGMENTATION);
    }
    
    /**
     * Extracts geometric structure.
     * 
     * @return geometric structure
     * @throws AnalysisException AnalysisException
     */
    public BxDocument getBxDocument() throws AnalysisException {
        doWork(Step.INITIAL_CLASSIFICATION);
        return BxModelUtils.deepClone(bxDocument);
    }
    
    /**
     * Extracts geometric structure with general labels.
     * 
     * @return geometric structure
     * @throws AnalysisException AnalysisException
     */
    public BxDocument getBxDocumentWithGeneralLabels() throws AnalysisException {
        doWork(Step.INITIAL_CLASSIFICATION);
        BxDocument doc = BxModelUtils.deepClone(bxDocument);
        for (BxZone zone : doc.asZones()) {
            zone.setLabel(zone.getLabel().getGeneralLabel());
        }
        return doc;
    }
    
    /**
     * Extracts geometric structure with specific labels.
     * 
     * @return geometric structure
     * @throws AnalysisException AnalysisException
     */
    public BxDocument getBxDocumentWithSpecificLabels() throws AnalysisException {
        doWork(Step.METADATA_CLASSIFICATION);
        doWork(Step.CONTENT_FILTERING);
        BxDocument doc = BxModelUtils.deepClone(bxDocument);
        for (BxZone zone : doc.asZones()) {
            if (BxZoneLabel.GEN_REFERENCES.equals(zone.getLabel())) {
                zone.setLabel(BxZoneLabel.REFERENCES);
            }
            if (BxZoneLabel.GEN_OTHER.equals(zone.getLabel())) {
                zone.setLabel(BxZoneLabel.OTH_UNKNOWN);
            }
        }
        return doc;
    }

    public List<BxImage> getImages(String imagesPrefix) throws AnalysisException {
        doWork(Step.CHARACTER_EXTRACTION);
        List<BxImage> images = Lists.newArrayList(bxDocument.asImages());
        for (BxImage image : images) {
            image.setPrefix(imagesPrefix);
        }
        return images;
    }
    
    /**
     * Extracts the metadata.
     * 
     * @return the metadata
     * @throws AnalysisException AnalysisException
     */
    public DocumentMetadata getMetadata() throws AnalysisException {
        doWork(Step.AFFILIATION_PARSING);
        return metadata;
    }
    
    /**
     * Extracts the metadata in NLM format.
     * 
     * @return the metadata in NLM format
     * @throws AnalysisException AnalysisException
     */
    public Element getMetadataAsNLM() throws AnalysisException {
        try {
            doWork(Step.AFFILIATION_PARSING);
            MetadataToNLMConverter converter = new MetadataToNLMConverter();
            return converter.convert(metadata);
        } catch (TransformationException ex) {
            throw new AnalysisException(ex);
        }
    }
    
    /**
     * Extracts the references.
     * 
     * @return the list of references
     * @throws AnalysisException AnalysisException
     */
    public List<BibEntry> getReferences() throws AnalysisException {
        doWork(Step.REFERENCE_PARSING);
        return references;
    }
  
    /**
     * Extracts the references in NLM format.
     * 
     * @return the list of references
     * @throws AnalysisException AnalysisException
     */
    public List<Element> getReferencesAsNLM() throws AnalysisException {
        doWork(Step.REFERENCE_PARSING);
        List<Element> refs = new ArrayList<Element>();
        BibEntryToNLMConverter converter = new BibEntryToNLMConverter();
        for (BibEntry ref : references) {
            try {
                refs.add(converter.convert(ref));
            } catch (TransformationException ex) {
                throw new AnalysisException(ex);
            }
        }
        return refs;
    }

    /**
     * Extracts raw text.
     * 
     * @return raw text
     * @throws AnalysisException AnalysisException
     */
    public String getRawFullText() throws AnalysisException {
        doWork(Step.READING_ORDER);
        return ContentCleaner.cleanAll(bxDocument.toText());
    }

    /**
     * Extracts labelled raw text.
     * 
     * @return labelled raw text
     * @throws AnalysisException AnalysisException
     */
    public Element getLabelledFullText() throws AnalysisException {
        doWork(Step.METADATA_CLASSIFICATION);
        doWork(Step.TOC_EXTRACTION);
        RawTextWithLabelsExtractor textExtractor = new RawTextWithLabelsExtractor();
        return textExtractor.extractRawTextWithLabels(bxDocument, bxBody);
    }
    
    /**
     * Extracts structured full text.
     * 
     * @return full text model
     * @throws AnalysisException AnalysisException
     */
    public ContentStructure getBody() throws AnalysisException {
        doWork(Step.CONTENT_CLEANING);
        return body;
    }
    
    /**
     * Extracts structured full text.
     * 
     * @param imagesPrefix images prefix
     * @return full text in NLM format
     * @throws AnalysisException AnalysisException
     */
    public Element getBodyAsNLM(String imagesPrefix) throws AnalysisException {
        try {
            doWork(Step.CITPOS_DETECTION);
            ModelToModelConverter<ContentStructure, Element> converter
                    = new DocContentStructToNLMElementConverter();
            if (imagesPrefix == null) {
                return converter.convert(body, citationPositions);
            }
            return converter.convert(body, citationPositions, getImages(imagesPrefix));
        } catch (TransformationException ex) {
            throw new AnalysisException(ex);
        }
    }

    /**
     * Extracts full content in NLM format.
     * 
     * @param imagesPrefix images prefix
     * @return full content in NLM format
     * @throws AnalysisException AnalysisException
     */
    public Element getContentAsNLM(String imagesPrefix) throws AnalysisException {
        doWork(Step.AFFILIATION_PARSING);
        doWork(Step.REFERENCE_PARSING);
        doWork(Step.CITPOS_DETECTION);
        
        Element nlmMetadata = getMetadataAsNLM();
        List<Element> nlmReferences = getReferencesAsNLM();
        Element nlmFullText = getBodyAsNLM(imagesPrefix);
        
        Element nlmContent = new Element("article");
        
        for (Object ns : nlmFullText.getAdditionalNamespaces()) {
            if (ns instanceof Namespace) {
                nlmContent.addNamespaceDeclaration((Namespace)ns);
            }
        }

        Element meta = (Element) nlmMetadata.getChild("front").clone();
        nlmContent.addContent(meta);
            
        nlmContent.addContent(nlmFullText);
            
        Element back = new Element("back");
        Element refList = new Element("ref-list");
        for (int i = 0; i < nlmReferences.size(); i++) {
            Element ref = nlmReferences.get(i);
            Element r = new Element("ref");
            r.setAttribute("id", "ref" + String.valueOf(i+1));
            r.addContent(ref);
            refList.addContent(r);
        }
        back.addContent(refList);
        nlmContent.addContent(back);
        return nlmContent;
    }
    
    private void doWork(Step step) throws AnalysisException {
        if (step == null || stepsDone.contains(step)) {
            return;
        }
        for (Step ps : step.getPrerequisites()) {
            doWork(ps);
        }
        switch (step) {
            case CHARACTER_EXTRACTION:
                if (pdfFile == null) {
                    throw new AnalysisException("No PDF document uploaded!");
                }
                bxDocument = ExtractionUtils.extractCharacters(conf, pdfFile);
                break;
            case PAGE_SEGMENTATION:
                bxDocument = ExtractionUtils.segmentPages(conf, bxDocument);
                break;
            case READING_ORDER:
                bxDocument = ExtractionUtils.resolveReadingOrder(conf, bxDocument);
                break;
            case INITIAL_CLASSIFICATION:
                bxDocument = ExtractionUtils.classifyInitially(conf, bxDocument);
                break;
            case METADATA_CLASSIFICATION:
                bxDocument = ExtractionUtils.classifyMetadata(conf, bxDocument);
                break;
            case METADATA_CLEANING:
                metadata = ExtractionUtils.cleanMetadata(conf, bxDocument);
                break;
            case AFFILIATION_PARSING:
                metadata = ExtractionUtils.parseAffiliations(conf, metadata);
                break;
            case REFERENCE_EXTRACTION:
                referenceStrings = ExtractionUtils.extractRefStrings(conf, bxDocument);
                break;
            case REFERENCE_PARSING:
                references = ExtractionUtils.parseReferences(conf, referenceStrings);
                break;
            case CONTENT_FILTERING:
                bxDocument = ExtractionUtils.filterContent(conf, bxDocument);
                break;
            case HEADER_DETECTION:
                bxBody = ExtractionUtils.extractHeaders(conf, bxDocument);
                break;
            case TOC_EXTRACTION:
                bxBody = ExtractionUtils.clusterHeaders(conf, bxBody);
                break;
            case CONTENT_CLEANING:
                body = ExtractionUtils.cleanStructure(conf, bxBody);
                break;
            case CITPOS_DETECTION:
                citationPositions = ExtractionUtils.findCitationPositions(conf, body, references);
                break;
            default: break;
        }
        stepsDone.add(step);
    }
    
    /**
     * Resets the extraction results.
     * 
     * @throws IOException IOException
     */
    public void reset() throws IOException {
        referenceStrings = null;
        bxBody = null;
        citationPositions = null;
        bxDocument = null;
        metadata = null;
        references = null;
        body = null;
        if (pdfFile != null) {
            pdfFile.close();
        }
        pdfFile = null;
        stepsDone.clear();
    }

    public ComponentConfiguration getConf() {
        return conf;
    }

    public void setConf(ComponentConfiguration conf) {
        this.conf = conf;
    }
}
