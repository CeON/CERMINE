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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.transformers.BibEntryToNLMElementConverter;
import pl.edu.icm.cermine.content.RawTextWithLabelsExtractor;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.content.transformers.BxContentStructToDocContentStructConverter;
import pl.edu.icm.cermine.content.transformers.DocContentStructToNLMElementConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.metadata.transformers.DocumentMetadataToNLMElementConverter;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * Extraction utility class
 *
 * @author Dominika Tkaczyk
 */
public class ExtractionUtils {
   
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
        BxDocument doc = conf.characterExtractor.extractCharacters(stream);
        doc = conf.documentSegmenter.segmentDocument(doc);
        doc = conf.readingOrderResolver.resolve(doc);
        return conf.initialClassifier.classifyZones(doc);
    }
    
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
            DocumentMetadataToNLMElementConverter converter = new DocumentMetadataToNLMElementConverter();
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
        BxDocument doc = conf.metadataClassifier.classifyZones(document);
        DocumentMetadata metadata = conf.metadataExtractor.extractMetadata(doc);
    	for (DocumentAffiliation aff : metadata.getAffiliations()) {
    		conf.affiliationParser.parse(aff);
    	}
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
            DocumentMetadataToNLMElementConverter converter = new DocumentMetadataToNLMElementConverter();
            return converter.convert(extractMetadata(conf, document));
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract metadata from the document!", ex);
        }
    }

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
        BxDocument doc = conf.characterExtractor.extractCharacters(stream);
        doc = conf.documentSegmenter.segmentDocument(doc);
        doc = conf.readingOrderResolver.resolve(doc);
        return extractRawText(conf, doc);
    }
    
    /**
     * Extracts raw text from document' box structure.
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
        String[] refs = conf.bibReferenceExtractor.extractBibReferences(document);
        BibEntry[] parsedRefs = new BibEntry[refs.length];
        for (int i = 0; i < refs.length; i++) {
            parsedRefs[i] = conf.bibReferenceParser.parseBibReference(refs[i]);
        }
        return parsedRefs;
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
        BibEntryToNLMElementConverter converter = new BibEntryToNLMElementConverter();
        for (BibEntry entry : entries) {
            try {
                elements.add(converter.convert(entry));
            } catch (TransformationException ex) {
                throw new AnalysisException("Cannot convert references!", ex);
            }
        }
        return elements.toArray(new Element[entries.length]);
    }
    
    /**
     * Extracts full text from input stream.
     * 
     * @param conf extraction configuration
     * @param stream PDF stream
     * @return document's full text in NLM format
     * @throws AnalysisException 
     */
    public static Element extractTextAsNLM(ComponentConfiguration conf, InputStream stream) 
            throws AnalysisException {
        try {
            ModelToModelConverter<DocumentContentStructure, Element> converter
                    = new DocContentStructToNLMElementConverter();
            DocumentContentStructure struct = extractText(conf, stream);
            return converter.convert(struct);
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract text from document!", ex);
        }
    }

    /**
     * Extracts full text from document's box structure.
     * 
     * @param conf extraction configuration
     * @param document box structure
     * @return document's full text in NLM format
     * @throws AnalysisException 
     */
    public static Element extractTextAsNLM(ComponentConfiguration conf, BxDocument document) 
            throws AnalysisException {
        try {
            ModelToModelConverter<DocumentContentStructure, Element> converter
                    = new DocContentStructToNLMElementConverter();
            DocumentContentStructure struct = extractText(conf, document);
            return converter.convert(struct);
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
    public static DocumentContentStructure extractText(ComponentConfiguration conf, InputStream stream) 
            throws AnalysisException {
        BxDocument doc = extractStructure(conf, stream);
        return extractText(conf, doc);
    }

    /**
     * Extracts full text from document's box structure.
     * 
     * @param conf extraction configuration
     * @param document box structure
     * @return document's full text
     * @throws AnalysisException 
     */
    public static DocumentContentStructure extractText(ComponentConfiguration conf, BxDocument document) 
            throws AnalysisException {
        try {
            BxDocument doc = conf.contentFilter.filter(document);
            BxDocContentStructure tmpContentStructure = conf.contentHeaderExtractor.extractHeaders(doc);
            conf.contentCleaner.cleanupContent(tmpContentStructure);
            BxContentStructToDocContentStructConverter converter = 
                    new BxContentStructToDocContentStructConverter();
            return converter.convert(tmpContentStructure);
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract content from the document!", ex);
        }
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
        BxDocument doc = conf.metadataClassifier.classifyZones(document);
        doc = conf.contentFilter.filter(doc);
        BxDocContentStructure contentStr = conf.contentHeaderExtractor.extractHeaders(doc);
        RawTextWithLabelsExtractor textExtractor = new RawTextWithLabelsExtractor();
        return textExtractor.extractRawTextWithLabels(document, contentStr);
    }
    
}
