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
import com.google.common.collect.Sets;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.content.citations.ContentStructureCitationPositions;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.transformers.BxContentToDocContentConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * Extraction utility class
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ExtractionUtils {

    private static void debug(ComponentConfiguration conf, double start, String msg) {
        if (conf.timeDebug) {
            double elapsed = (System.currentTimeMillis() - start) / 1000.;
            System.out.println(msg + ": " + elapsed);
        }
    }
    
    //1.1 Character extraction
    public static BxDocument extractCharacters(ComponentConfiguration conf, InputStream stream) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        BxDocument doc = conf.getCharacterExtractor().extractCharacters(stream);
        debug(conf, start, "1.1 Character extraction");
        return doc;
    }
    
    //1.2 Page segmentation
    public static BxDocument segmentPages(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        TimeoutRegister.get().check();
        doc = conf.getDocumentSegmenter().segmentDocument(doc);
        debug(conf, start, "1.2 Page segmentation");
        return doc;
    }
    
    //1.3 Reading order resolving
    public static BxDocument resolveReadingOrder(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        doc = conf.getReadingOrderResolver().resolve(doc);
        debug(conf, start, "1.3 Reading order resolving");
        return doc;
    }
    
    //1.4 Initial classification
    public static BxDocument classifyInitially(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        doc = conf.getInitialClassifier().classifyZones(doc);
        debug(conf, start, "1.4 Initial classification");
        return doc;
    }
    
    //2.1 Metadata classification
    public static BxDocument classifyMetadata(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        doc = conf.getMetadataClassifier().classifyZones(doc);
        debug(conf, start, "2.1 Metadata classification");
        return doc;
    }
    
    //2.2 Metadata cleaning
    public static DocumentMetadata cleanMetadata(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        DocumentMetadata metadata = conf.getMetadataExtractor().extractMetadata(doc);
        debug(conf, start, "2.2 Metadata cleaning");
        return metadata;
    }
    
    //2.3 Affiliation parsing
    public static DocumentMetadata parseAffiliations(ComponentConfiguration conf, DocumentMetadata metadata)
            throws AnalysisException {
        long start = System.currentTimeMillis();
    	for (DocumentAffiliation aff : metadata.getAffiliations()) {
            conf.getAffiliationParser().parse(aff);
    	}
        debug(conf, start, "2.3 Affiliation parsing");
        return metadata;
    }
    
    //3.1 Reference extraction
    public static List<String> extractRefStrings(ComponentConfiguration conf, BxDocument doc)
            throws AnalysisException {
        long start = System.currentTimeMillis();
        String[] refs = conf.getBibRefExtractor().extractBibReferences(doc);
        List<String> references = Lists.newArrayList(refs);
        debug(conf, start, "3.1 Reference extraction");
        return references;
    }

    //3.2 Reference parsing
    public static List<BibEntry> parseReferences(ComponentConfiguration conf, List<String> refs)
            throws AnalysisException {
        long start = System.currentTimeMillis();
        List<BibEntry> parsedRefs = new ArrayList<BibEntry>();
        for (String ref : refs) {
            parsedRefs.add(conf.getBibRefParser().parseBibReference(ref));
        }
        debug(conf, start, "3.2 Reference parsing");
        return parsedRefs;
    }
    
    //4.1 Content filtering
    public static BxDocument filterContent(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        doc = conf.getContentFilter().filter(doc);
        debug(conf, start, "4.1 Content filtering");
        return doc;
    }
    
    //4.2 Headers extraction
    public static BxContentStructure extractHeaders(ComponentConfiguration conf, BxDocument doc) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        BxContentStructure contentStructure = conf.getContentHeaderExtractor().extractHeaders(doc);
        debug(conf, start, "4.2 Headers extraction");
        return contentStructure;
    }
    
    //4.3 Headers clustering
    public static BxContentStructure clusterHeaders(ComponentConfiguration conf, BxContentStructure contentStructure) 
            throws AnalysisException {
        long start = System.currentTimeMillis();
        conf.getContentHeaderClusterizer().clusterHeaders(contentStructure);
        debug(conf, start, "4.3 Headers clustering");
        return contentStructure;
    }
    
    //4.4 Content cleaner
    public static ContentStructure cleanStructure(ComponentConfiguration conf, BxContentStructure contentStructure) 
            throws AnalysisException {
        try {
            long start = System.currentTimeMillis();
            conf.getContentCleaner().cleanupContent(contentStructure);
            BxContentToDocContentConverter converter = new BxContentToDocContentConverter();
            ContentStructure structure = converter.convert(contentStructure);
            debug(conf, start, "4.4 Content cleaning");
            return structure;
        } catch (TransformationException ex) {
            throw new AnalysisException(ex);
        }
    }

    //4.5 Citation positions finding
    public static ContentStructureCitationPositions findCitationPositions(ComponentConfiguration conf, 
            ContentStructure struct, List<BibEntry> citations) {
        long start = System.currentTimeMillis();
        ContentStructureCitationPositions positions = conf.getCitationPositionFinder().findReferences(struct, citations);
        debug(conf, start, "4.5 Citation positions finding");
        return positions;
    }
  
    public enum Step {
        
        CHARACTER_EXTRACTION    (null),
        
        PAGE_SEGMENTATION       (setOf(CHARACTER_EXTRACTION)),
        
        READING_ORDER           (setOf(PAGE_SEGMENTATION)),
        
        INITIAL_CLASSIFICATION  (setOf(READING_ORDER)),
     
        METADATA_CLASSIFICATION (setOf(INITIAL_CLASSIFICATION)),
        
        METADATA_CLEANING       (setOf(METADATA_CLASSIFICATION)),
        
        AFFIIATION_PARSING      (setOf(METADATA_CLEANING)),
        
        REFERENCE_EXTRACTION    (setOf(INITIAL_CLASSIFICATION)),
        
        REFERENCE_PARSING       (setOf(REFERENCE_EXTRACTION)),
        
        CONTENT_FILTERING       (setOf(INITIAL_CLASSIFICATION)),
        
        HEADER_DETECTION        (setOf(CONTENT_FILTERING)),
        
        TOC_EXTRACTION          (setOf(HEADER_DETECTION)),
        
        CONTENT_CLEANING        (setOf(TOC_EXTRACTION)),
        
        CITPOS_DETECTION        (setOf(CONTENT_CLEANING, REFERENCE_PARSING));
        
        private Set<Step> prerequisites;

        Step(Set<Step> prerequisites) {
            this.prerequisites = prerequisites;
        }

        public Set<Step> getPrerequisites() {
            return prerequisites;
        }
        
        private static Set<Step> setOf(Step... steps) {
            return Sets.newHashSet(steps);
        }

        static {
            for (Step s : values()) {
                if (s.prerequisites == null) {
                    s.prerequisites = EnumSet.noneOf(Step.class);
                } else {
                    s.prerequisites = EnumSet.copyOf(s.prerequisites);
                }
            }
        }
        
    }
    
}
