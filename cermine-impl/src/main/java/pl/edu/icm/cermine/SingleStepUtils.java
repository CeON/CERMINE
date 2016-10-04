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
import java.util.List;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.content.citations.CitationPosition;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * Extraction utility class
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SingleStepUtils {
   
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

    //4.4 Citation positions finding
    public static List<List<CitationPosition>> findCitationPositions(ComponentConfiguration conf, 
            String fullText, List<BibEntry> citations) {
        return conf.citationPositionFinder.findReferences(fullText, citations);
    }
    
}
