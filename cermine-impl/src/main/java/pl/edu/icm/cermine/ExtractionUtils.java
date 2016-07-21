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
import pl.edu.icm.cermine.bibref.sentiment.model.CitationPosition;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationSentiment;
import pl.edu.icm.cermine.bibref.transformers.BibEntryToNLMConverter;
import pl.edu.icm.cermine.content.RawTextWithLabelsExtractor;
import pl.edu.icm.cermine.content.citations.ContentCitationPositionFinder;
import pl.edu.icm.cermine.content.citations.ContentStructureCitationPositions;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.transformers.BxContentToDocContentConverter;
import pl.edu.icm.cermine.content.transformers.DocContentStructToNLMElementConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
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
        long start = System.currentTimeMillis();
        BxDocument doc = extractCharacters(conf, stream);
        TimeoutRegister.get().check();
        doc = segmentPages(conf, doc);
        doc = resolveReadingOrder(conf, doc);
        doc =  classifyInitially(conf, doc);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("1. Structure extraction: " + elapsed);
        }
        return doc;
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
        long start = System.currentTimeMillis();
        BxDocument doc = classifyMetadata(conf, document);
        DocumentMetadata metadata = cleanMetadata(conf, doc);
    	metadata = parseAffiliations(conf, metadata);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("2. Metadata extraction: " + elapsed);
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
            MetadataToNLMConverter converter = new MetadataToNLMConverter();
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
        long start = System.currentTimeMillis();
        BxDocument doc = extractCharacters(conf, stream);
        doc = segmentPages(conf, doc);
        doc = resolveReadingOrder(conf, doc);
        String text = extractRawText(conf, doc);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("Raw text extraction: " + elapsed);
        }
        return text;
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
        long start = System.currentTimeMillis();
        String[] refs = extractRefStrings(conf, document);
        BibEntry[] parsed =  parseReferences(conf, refs);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("3. References extraction: " + elapsed);
        }
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
        BxDocument doc = extractStructure(conf, stream);
        return extractTextAsNLM(conf, doc, null);
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
    public static Element extractTextAsNLM(ComponentConfiguration conf, BxDocument document,
            List<BibEntry> references) 
            throws AnalysisException {
        try {
            ModelToModelConverter<ContentStructure, Element> converter
                    = new DocContentStructToNLMElementConverter();
            ContentStructure struct = extractText(conf, document);
            if (references == null) {
                references = Arrays.asList(extractReferences(conf, document));
            }
            ContentCitationPositionFinder finder = new ContentCitationPositionFinder();
            ContentStructureCitationPositions positions = finder.findReferences(struct, references);
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
    public static ContentStructure extractText(ComponentConfiguration conf, InputStream stream) 
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
    public static ContentStructure extractText(ComponentConfiguration conf, BxDocument document) 
            throws AnalysisException {
        try {
            long start = System.currentTimeMillis();
            BxDocument doc = filterContent(conf, document);
            BxContentStructure tmpContentStructure = extractHeaders(conf, doc);
            tmpContentStructure = clusterHeaders(conf, tmpContentStructure);
            conf.contentCleaner.cleanupContent(tmpContentStructure);
            BxContentToDocContentConverter converter = 
                    new BxContentToDocContentConverter();
            ContentStructure structure = converter.convert(tmpContentStructure);
            if (conf.timeDebug) {
                double elapsed = (System.currentTimeMillis() - start) / 1000.;
                System.out.println("4. Body extraction: " + elapsed);
            }
            return structure;
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
        long start = System.currentTimeMillis();
        BxDocument doc = classifyMetadata(conf, document);
        doc = filterContent(conf, doc);
        BxContentStructure contentStr = extractHeaders(conf, doc);
        contentStr = clusterHeaders(conf, contentStr);
        RawTextWithLabelsExtractor textExtractor = new RawTextWithLabelsExtractor();
        Element rawText = textExtractor.extractRawTextWithLabels(document, contentStr);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("Raw text with labels extraction: " + elapsed);
        }
        return rawText;
    }
    
    
    //Single workflow steps
    
    //1.1 Character extraction
    public static BxDocument extractCharacters(ComponentConfiguration conf, InputStream stream) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        BxDocument doc = conf.characterExtractor.extractCharacters(stream);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("1.1 Character extraction: "+elapsed);
        }
        return doc;
    }
    
    //1.2 Page segmentation
    public static BxDocument segmentPages(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        TimeoutRegister.get().check();
        doc = conf.documentSegmenter.segmentDocument(doc);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("1.2 Page segmentation: "+elapsed);
        }
        return doc;
    }
    
    //1.3 Reading order resolving
    public static BxDocument resolveReadingOrder(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        doc = conf.readingOrderResolver.resolve(doc);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("1.3 Reading order resolving: "+elapsed);
        }
        return doc;
    }
    
    //1.4 Initial classification
    public static BxDocument classifyInitially(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        doc = conf.initialClassifier.classifyZones(doc);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("1.4 Initial classification: "+elapsed);
        }
        return doc;
    }
    
    //2.1 Metadata classification
    public static BxDocument classifyMetadata(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        doc = conf.metadataClassifier.classifyZones(doc);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("2.1 Metadata classification: "+elapsed);
        }
        return doc;
    }
    
    //2.2 Metadata cleaning
    public static DocumentMetadata cleanMetadata(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        DocumentMetadata metadata = conf.metadataExtractor.extractMetadata(doc);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("2.2 Metadata cleaning: "+elapsed);
        }
        return metadata;
    }
    
    //2.3 Affiliation parsing
    public static DocumentMetadata parseAffiliations(ComponentConfiguration conf, DocumentMetadata metadata)
            throws AnalysisException {
        long start = System.currentTimeMillis();
    	for (DocumentAffiliation aff : metadata.getAffiliations()) {
    		conf.affiliationParser.parse(aff);
    	}
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("2.3 Affiliation parsing: "+elapsed);
        }
        return metadata;
    }
    
    //3.1 Reference extraction
    public static String[] extractRefStrings(ComponentConfiguration conf, BxDocument doc)
            throws AnalysisException {
        long start = System.currentTimeMillis();
        String[] refs = conf.bibReferenceExtractor.extractBibReferences(doc);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("3.1 Reference extraction: "+elapsed);
        }
        return refs;
    }

    /**
     * Extracts citation sentiments.
     * 
     * @param conf extraction configuration
     * @param fullText the full text
     * @param citations citation list
     * @return citation sentiments
     */
    public static List<CitationSentiment> analyzeSentiment(ComponentConfiguration conf, 
            String fullText, List<BibEntry> citations) {
        List<List<CitationPosition>> positions = findCitationPositions(conf, fullText, citations);
        return analyzeSentimentFromPositions(conf, fullText, positions);
    }

    /**
     * Extracts citation sentiments.
     * 
     * @param conf extraction configuration
     * @param fullText the full text
     * @param positions citation positions
     * @return  citation sentiments
     */
    public static List<CitationSentiment> analyzeSentimentFromPositions(ComponentConfiguration conf, 
            String fullText, List<List<CitationPosition>> positions) {
        List<CitationSentiment> sentiments = new ArrayList<CitationSentiment>(positions.size());
        List<List<String>> contexts = conf.citationContextFinder.findContext(fullText, positions);
        for (List<String> context : contexts) {
            sentiments.add(conf.citationSentimentAnalyser.analyzeSentiment(context));
        }
        return sentiments;
    }

    /**
     * Extracts citation locations.
     * 
     * @param conf extraction configuration
     * @param fullText the full text
     * @param citations citation list
     * @return  citation loations
     */
    public static List<List<CitationPosition>> findCitationPositions(ComponentConfiguration conf, 
            String fullText, List<BibEntry> citations) {
        List<List<CitationPosition>> positions = conf.citationPositionFinder.findReferences(fullText, citations);
        return positions;
    }
    
    //3.2 Reference parsing
    public static BibEntry[] parseReferences(ComponentConfiguration conf, String[] refs)
            throws AnalysisException {
        long start = System.currentTimeMillis();
        BibEntry[] parsedRefs = new BibEntry[refs.length];
        for (int i = 0; i < refs.length; i++) {
            parsedRefs[i] = conf.bibReferenceParser.parseBibReference(refs[i]);
        }
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("3.2 Reference parsing: "+elapsed);
        }
        return parsedRefs;
    }
    
    //4.1 Content filtering
    public static BxDocument filterContent(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        doc = conf.contentFilter.filter(doc);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("4.1 Content filtering: "+elapsed);
        }
        return doc;
    }
    
    //4.2 Headers extraction
    public static BxContentStructure extractHeaders(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        BxContentStructure contentStructure = conf.contentHeaderExtractor.extractHeaders(doc);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("4.2 Headers extraction: "+elapsed);
        }
        return contentStructure;
    }
    
    //4.3 Headers clustering
    public static BxContentStructure clusterHeaders(ComponentConfiguration conf, BxContentStructure contentStructure) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        conf.contentHeaderClusterizer.clusterHeaders(contentStructure);
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println("4.3 Headers clustering: "+elapsed);
        }
        return contentStructure;
    }

}
