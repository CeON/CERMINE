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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.transformers.BibEntryToNLMConverter;
import pl.edu.icm.cermine.content.RawTextWithLabelsExtractor;
import pl.edu.icm.cermine.content.citations.ContentStructureCitationPositions;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.transformers.BxContentToDocContentConverter;
import pl.edu.icm.cermine.content.transformers.DocContentStructToNLMElementConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.metadata.transformers.MetadataToNLMConverter;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * Extraction utility class
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ExtractionUtils {
   
    /*
        Box structure
    */
    
    /**
     * Extracts box structure from input stream.
     * 
     * @param conf extraction configuration
     * @param stream PDF stream
     * @return box structure
     * @throws AnalysisException 
     */
    public static BxDocument extractStructure(ComponentConfiguration conf, InputStream stream) 
            throws AnalysisException {
        BxDocument doc = SingleStepUtils.extractCharacters(conf, stream);
        TimeoutRegister.get().check();
        doc = SingleStepUtils.segmentPages(conf, doc);
        doc = SingleStepUtils.resolveReadingOrder(conf, doc);
        doc =  SingleStepUtils.classifyInitially(conf, doc);
        return doc;
    }
    
    
    /*
        Basic metadata
    */
    
    /**
     * Extracts metadata from input stream.
     * 
     * @param conf extraction configuration
     * @param stream PDF stream
     * @return document's metadata
     * @throws AnalysisException 
     */
    public static DocumentMetadata extractMetadata(ComponentConfiguration conf, InputStream stream) 
            throws AnalysisException {
        BxDocument doc = extractStructure(conf, stream);
        return extractMetadata(conf, doc);
    }
    
    /**
     * Extracts NLM metadata from input stream.
     * 
     * @param conf extraction configuration
     * @param stream PDF stream
     * @return document's metadata in NLM format
     * @throws AnalysisException 
     */
    public static Element extractMetadataAsNLM(ComponentConfiguration conf, InputStream stream) 
            throws AnalysisException {
        try {
            MetadataToNLMConverter converter = new MetadataToNLMConverter();
            return converter.convert(extractMetadata(conf, stream));
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract metadata from the document!", ex);
        }
    }
    
    /**
     * Extracts metadata from document's structure.
     * 
     * @param conf extraction configuration
     * @param document box structure
     * @return document's metadata
     * @throws AnalysisException 
     */
    public static DocumentMetadata extractMetadata(ComponentConfiguration conf, BxDocument document) 
            throws AnalysisException {
        BxDocument doc = SingleStepUtils.classifyMetadata(conf, document);
        DocumentMetadata metadata = SingleStepUtils.cleanMetadata(conf, doc);
    	metadata = SingleStepUtils.parseAffiliations(conf, metadata);
        return metadata;
    }
    
    /**
     * Extracts NLM metadata from document's structure.
     * 
     * @param conf extraction configuration
     * @param document box structure
     * @return document's metadata in NLM format
     * @throws AnalysisException 
     */
    public static Element extractMetadataAsNLM(ComponentConfiguration conf, BxDocument document) 
            throws AnalysisException {
        try {
            MetadataToNLMConverter converter = new MetadataToNLMConverter();
            return converter.convert(extractMetadata(conf, document));
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract metadata from the document!", ex);
        }
    }


    /*
        References
    */
    
    /**
     * Extracts references from input stream.
     * 
     * @param conf extraction configuration
     * @param stream PDF stream
     * @return document's references
     * @throws AnalysisException 
     */
    public static BibEntry[] extractReferences(ComponentConfiguration conf, InputStream stream)
            throws AnalysisException {
        BxDocument doc = extractStructure(conf, stream);
        return extractReferences(conf, doc);
    }

    /**
     * Extracts references from document's box structure.
     * 
     * @param conf extraction configuration
     * @param document box structure
     * @return document's references
     * @throws AnalysisException 
     */
    public static BibEntry[] extractReferences(ComponentConfiguration conf, BxDocument document)
            throws AnalysisException {
        String[] refs = SingleStepUtils.extractRefStrings(conf, document);
        BibEntry[] parsed =  SingleStepUtils.parseReferences(conf, refs);
        return parsed;
    }
    
    /**
     * Extracts references from input stream.
     * 
     * @param conf extraction configuration
     * @param stream PDF stream
     * @return document's references in NLM format
     * @throws AnalysisException 
     */
    public static Element[] extractReferencesAsNLM(ComponentConfiguration conf, InputStream stream)
            throws AnalysisException {
        return convertReferences(extractReferences(conf, stream));
    }

    /**
     * Extracts references from document's box structure.
     * 
     * @param conf extraction configuration
     * @param document box structure
     * @return document's references in NLM format
     * @throws AnalysisException 
     */
    public static Element[] extractReferencesAsNLM(ComponentConfiguration conf, BxDocument document) 
            throws AnalysisException {
        return convertReferences(extractReferences(conf, document));
    }
    
    /**
     * Converts references from BibEntry model to NLM
     * 
     * @param entries BibEntry objects
     * @return bib entries in NLM format
     * @throws AnalysisException 
     */
    public static Element[] convertReferences(BibEntry[] entries) 
            throws AnalysisException {
        List<Element> elements = new ArrayList<Element>(entries.length);
        BibEntryToNLMConverter converter = new BibEntryToNLMConverter();
        for (BibEntry entry : entries) {
            try {
                elements.add(converter.convert(entry));
            } catch (TransformationException ex) {
                throw new AnalysisException("Cannot convert references!", ex);
            }
        }
        return elements.toArray(new Element[entries.length]);
    }
    
    
    /*
        Body
    */

    /**
     * Extracts full text from input stream.
     * 
     * @param conf extraction configuration
     * @param stream PDF stream
     * @return document's full text in NLM format
     * @throws AnalysisException 
     */
    public static Element extractBodyAsNLM(ComponentConfiguration conf, InputStream stream) 
            throws AnalysisException {
        BxDocument doc = extractStructure(conf, stream);
        return extractBodyAsNLM(conf, doc, null);
    }

    /**
     * Extracts full text from document's box structure.
     * 
     * @param conf extraction configuration
     * @param document box structure
     * @param references references
     * @return document's full text in NLM format
     * @throws AnalysisException 
     */
    public static Element extractBodyAsNLM(ComponentConfiguration conf, BxDocument document,
            List<BibEntry> references) 
            throws AnalysisException {
        try {
            ModelToModelConverter<ContentStructure, Element> converter
                    = new DocContentStructToNLMElementConverter();
            ContentStructure struct = extractBody(conf, document);
            if (references == null) {
                references = Arrays.asList(extractReferences(conf, document));
            }
            ContentStructureCitationPositions positions = conf.getCitationPositionFinder().findReferences(struct, references);
            return converter.convert(struct, positions);
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract text from document!", ex);
        }
    }
    
    /**
     * Extracts full text from input stream.
     * 
     * @param conf extraction configuration
     * @param stream PDF stream
     * @return document's full text
     * @throws AnalysisException 
     */
    public static ContentStructure extractBody(ComponentConfiguration conf, InputStream stream) 
            throws AnalysisException {
        BxDocument doc = extractStructure(conf, stream);
        return extractBody(conf, doc);
    }

    /**
     * Extracts full text from document's box structure.
     * 
     * @param conf extraction configuration
     * @param document box structure
     * @return document's full text
     * @throws AnalysisException 
     */
    public static ContentStructure extractBody(ComponentConfiguration conf, BxDocument document) 
            throws AnalysisException {
        try {
            BxDocument doc = SingleStepUtils.filterContent(conf, document);
            BxContentStructure tmpContentStructure = SingleStepUtils.extractHeaders(conf, doc);
            tmpContentStructure = SingleStepUtils.clusterHeaders(conf, tmpContentStructure);
            SingleStepUtils.cleanStructure(conf, tmpContentStructure);
            BxContentToDocContentConverter converter = 
                    new BxContentToDocContentConverter();
            ContentStructure structure = converter.convert(tmpContentStructure);
            return structure;
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract content from the document!", ex);
        }
    }    
    
    
    /*
        Raw text
    */
    
    /**
     * Extracts raw text from input stream.
     * 
     * @param conf extraction configuration
     * @param stream PDF stream
     * @return raw text content
     * @throws AnalysisException 
     */
    public static String extractRawText(ComponentConfiguration conf, InputStream stream)
            throws AnalysisException {
        BxDocument doc = SingleStepUtils.extractCharacters(conf, stream);
        doc = SingleStepUtils.segmentPages(conf, doc);
        doc = SingleStepUtils.resolveReadingOrder(conf, doc);
        String text = extractRawText(conf, doc);
        return text;
    }
    
    /**
     * Extracts raw text from document's box structure.
     * 
     * @param conf extraction configuration
     * @param document box structure
     * @return raw text content
     * @throws AnalysisException 
     */
    public static String extractRawText(ComponentConfiguration conf, BxDocument document) 
            throws AnalysisException {
        return ContentCleaner.cleanAll(document.toText());
    }
    
    /**
     * Extracts raw text with labels from input stream.
     * 
     * @param conf extraction configuration
     * @param stream PDF stream
     * @return raw text with labels
     * @throws AnalysisException 
     */
    public static Element extractRawTextWithLabels(ComponentConfiguration conf, InputStream stream) 
            throws AnalysisException {
        BxDocument doc = extractStructure(conf, stream);
        return extractRawTextWithLabels(conf, doc);
    }
    
    /**
     * Extracts raw text with labels from document's box structure.
     * 
     * @param conf extraction configuration
     * @param document document's box structure
     * @return raw text with labels
     * @throws AnalysisException 
     */
    public static Element extractRawTextWithLabels(ComponentConfiguration conf, BxDocument document) 
            throws AnalysisException {
        BxDocument doc = SingleStepUtils.classifyMetadata(conf, document);
        doc = SingleStepUtils.filterContent(conf, doc);
        BxContentStructure contentStr = SingleStepUtils.extractHeaders(conf, doc);
        contentStr = SingleStepUtils.clusterHeaders(conf, contentStr);
        RawTextWithLabelsExtractor textExtractor = new RawTextWithLabelsExtractor();
        Element rawText = textExtractor.extractRawTextWithLabels(document, contentStr);
        return rawText;
    }

}
