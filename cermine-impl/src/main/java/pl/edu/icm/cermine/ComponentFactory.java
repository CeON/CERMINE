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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import pl.edu.icm.cermine.bibref.BibReferenceExtractor;
import pl.edu.icm.cermine.bibref.BibReferenceParser;
import pl.edu.icm.cermine.bibref.CRFBibReferenceParser;
import pl.edu.icm.cermine.bibref.KMeansBibReferenceExtractor;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.content.citations.ContentCitationPositionFinder;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.filtering.ContentFilter;
import pl.edu.icm.cermine.content.filtering.SVMContentFilter;
import pl.edu.icm.cermine.content.headers.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.EnhancerMetadataExtractor;
import pl.edu.icm.cermine.metadata.MetadataExtractor;
import pl.edu.icm.cermine.metadata.affiliation.CRFAffiliationParser;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.parsing.tools.ParsableStringParser;
import pl.edu.icm.cermine.structure.*;
import pl.edu.icm.cermine.tools.ResourceUtils;

/**
 * A factory of extraction components.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ComponentFactory {

    /**
     * The method creates an instance of a default character extractor.
     * 
     * @return character extractor
     */
    public static CharacterExtractor getCharacterExtractor() {
        return new ITextCharacterExtractor();
    }
    
    /**
     * The method creates an instance of a default page segmenter.
     * 
     * @return page segmenter
     */
    public static DocumentSegmenter getDocumentSegmenter() {
        return new ParallelDocstrumSegmenter();
    }
    
    /**
     * The method creates an instance of a default reading order resolver.
     * 
     * @return reading order resolver
     */
    public static ReadingOrderResolver getReadingOrderResolver() {
        return new HierarchicalReadingOrderResolver();
    }
    
    /**
     * The method creates an instance of an initial zone classifier.
     * 
     * @param modelPath Path to svm model file (can be regular file or a classpath resource).
     *      For classpath resource, path should be prefixed with <code>classpath:</code>
     * @param rangePath Path to svm range file (can be regular file or a classpath resource).
     *      For classpath resource, path should be prefixed with <code>classpath:</code>
     * @return initial zone classifier
     * @throws AnalysisException AnalysisException
     * @throws IOException IOException
     */
    public static ZoneClassifier getInitialZoneClassifier(String modelPath, String rangePath) throws AnalysisException, IOException {
        
        return getInitialZoneClassifier(ResourceUtils.openResourceStream(modelPath), ResourceUtils.openResourceStream(rangePath));
    }
    
    /**
     * The method creates an instance of an initial zone classifier.
     * 
     * @param model svm model
     * @param range svm range file
     * @return initial zone classifier
     * @throws AnalysisException AnalysisException
     * @throws IOException IOException
     */
    public static ZoneClassifier getInitialZoneClassifier(InputStream model, InputStream range)
            throws AnalysisException, IOException {
        BufferedReader modelFileBR = new BufferedReader(new InputStreamReader(model));
        BufferedReader rangeFileBR = new BufferedReader(new InputStreamReader(range));
        ZoneClassifier classifier;
        try {
            classifier = new SVMInitialZoneClassifier(modelFileBR, rangeFileBR);
        } finally {
            modelFileBR.close();
            rangeFileBR.close();
        }
        return classifier;
    }
    
    /**
     * The method creates an instance of a metadata zone classifier.
     * 
     * @param modelPath Path to svm model file (can be regular file or a classpath resource).
     *      For classpath resource, path should be prefixed with <code>classpath:</code>
     * @param rangePath Path to svm range file (can be regular file or a classpath resource).
     *      For classpath resource, path should be prefixed with <code>classpath:</code>
     * @return metadata zone classifier
     * @throws AnalysisException AnalysisException
     * @throws IOException IOException
     */
    public static ZoneClassifier getMetadataZoneClassifier(String modelPath, String rangePath) throws AnalysisException, IOException {
        
        return getMetadataZoneClassifier(ResourceUtils.openResourceStream(modelPath), ResourceUtils.openResourceStream(rangePath));
    }
    
    /**
     * The method creates an instance of a metadata zone classifier.
     * 
     * @param model svm model
     * @param range svm range file
     * @return metadata zone classifier
     * @throws AnalysisException AnalysisException
     * @throws IOException IOException
     */
    public static ZoneClassifier getMetadataZoneClassifier(InputStream model, InputStream range) 
            throws AnalysisException, IOException {
        BufferedReader modelFileBR = new BufferedReader(new InputStreamReader(model));
        BufferedReader rangeFileBR = new BufferedReader(new InputStreamReader(range));
        ZoneClassifier classifier;
        try {
            classifier = new SVMMetadataZoneClassifier(modelFileBR, rangeFileBR);
        } finally {
            modelFileBR.close();
            rangeFileBR.close();
        }
        return classifier;
    }
    
    /**
     * The method creates an instance of a default metadata extractor.
     * 
     * @return metadata extractor
     */
    public static MetadataExtractor<DocumentMetadata> getMetadataExtractor() {
        return new EnhancerMetadataExtractor();
    }
    
    /**
     * The method creates an instance of a default affiliation parser.
     * 
     * @return affiliation parser
     * @throws AnalysisException AnalysisException
     */
    public static ParsableStringParser<DocumentAffiliation> getAffiliationParser() throws AnalysisException {
        return new CRFAffiliationParser();
    }
    
    /**
     * The method creates an instance of a default bib reference extractor.
     * 
     * @return bib reference extractor
     */
    public static BibReferenceExtractor getBibReferenceExtractor() {
        return new KMeansBibReferenceExtractor();
    }

    /**
     * The method creates an instance of a default bib reference parser.
     * 
     * @return bib reference parser
     * @throws AnalysisException AnalysisException
     */
    public static BibReferenceParser<BibEntry> getBibReferenceParser() throws AnalysisException {
        return CRFBibReferenceParser.getInstance();
    }

    /**
     * The method creates an instance of a bib reference parser.
     * 
     * @param model crf model 
     * @return parser
     * @throws AnalysisException AnalysisException
     */
    public static BibReferenceParser<BibEntry> getBibReferenceParser(InputStream model) throws AnalysisException {
        return new CRFBibReferenceParser(model);
    }

    /**
     * The method creates an instance of a default content filter. 
     * 
     * @param modelPath Path to svm model file (can be regular file or a classpath resource).
     *      For classpath resource, path should be prefixed with <code>classpath:</code>
     * @param rangePath Path to svm range file (can be regular file or a classpath resource).
     *      For classpath resource, path should be prefixed with <code>classpath:</code>
     * @return content filter
     * @throws AnalysisException AnalysisException
     * @throws IOException IOException
     */
    public static ContentFilter getContentFilter(String modelPath, String rangePath) throws AnalysisException, IOException {
        return getContentFilter(ResourceUtils.openResourceStream(modelPath), ResourceUtils.openResourceStream(rangePath));
    }

    /**
     * The method creates an instance of a content filter. 
     * 
     * @param model svm model file
     * @param range svm range file
     * @return content filter
     * @throws AnalysisException AnalysisException
     * @throws IOException IOException
     */
    public static ContentFilter getContentFilter(InputStream model, InputStream range) 
            throws AnalysisException, IOException {
        BufferedReader modelFileBR = new BufferedReader(new InputStreamReader(model));
        BufferedReader rangeFileBR = new BufferedReader(new InputStreamReader(range));
        ContentFilter filter;
        try {
            filter = new SVMContentFilter(modelFileBR, rangeFileBR);
        } finally {
            modelFileBR.close();
            rangeFileBR.close();
        }
        return filter;
    }
    
    /**
     * The method creates an instance of a default content header extractor. 
     * 
     * @return content header extractor
     * @throws AnalysisException AnalysisException
     */
    public static ContentHeadersExtractor getContentHeaderExtractor() throws AnalysisException {
        return new HeuristicContentHeadersExtractor();
    }

    public static HeadersClusterizer getContentHeaderClusterizer() throws AnalysisException {
        return new SimpleHeadersClusterizer();
    }
    
    /**
     * The method creates an instance of a content header extractor. 
     * 
     * @param model svm model file
     * @param range svm range file
     * @return content header extractor
     * @throws AnalysisException AnalysisException
     * @throws IOException IOException
     */
    public static ContentHeadersExtractor getContentHeaderExtractor(InputStream model, InputStream range) 
            throws AnalysisException, IOException {
        BufferedReader modelFileBR = new BufferedReader(new InputStreamReader(model));
        BufferedReader rangeFileBR = new BufferedReader(new InputStreamReader(range));
        ContentHeadersExtractor extractor;
        try {
            extractor = new SVMContentHeadersExtractor(modelFileBR, rangeFileBR);
        } finally {
            modelFileBR.close();
            rangeFileBR.close();
        }
        return extractor;
    }
    
    /**
     * The method creates an instance of a default content cleaner.
     * 
     * @return content cleaner
     */
    public static ContentCleaner getContentCleaner() {
        return new ContentCleaner();
    }
    
    /**
     * The method creates an instance of the default citation reference finder.
     * 
     * @return citation reference finder
     */
    public static ContentCitationPositionFinder getCitationPositionFinder() {
        return new ContentCitationPositionFinder();
    }
    
}
