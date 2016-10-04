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

import java.io.IOException;
import java.io.InputStream;
import pl.edu.icm.cermine.bibref.BibReferenceExtractor;
import pl.edu.icm.cermine.bibref.BibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.configuration.ContentExtractorConfig;
import pl.edu.icm.cermine.configuration.ContentExtractorConfig.ConfigurationProperty;
import pl.edu.icm.cermine.content.citations.ContentCitationPositionFinder;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.filtering.ContentFilter;
import pl.edu.icm.cermine.content.headers.ContentHeadersExtractor;
import pl.edu.icm.cermine.content.headers.HeadersClusterizer;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.MetadataExtractor;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.parsing.tools.ParsableStringParser;
import pl.edu.icm.cermine.structure.CharacterExtractor;
import pl.edu.icm.cermine.structure.DocumentSegmenter;
import pl.edu.icm.cermine.structure.ReadingOrderResolver;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * The class represents the configuration of the extraction system.
 * It contains all the objects performing the individual steps
 * of an extraction task.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ComponentConfiguration {

    /** character extractor */
    private CharacterExtractor characterExtractor;
    
    /** document object segmenter */
    private DocumentSegmenter documentSegmenter;
    
    /** reading order resolver */
    private ReadingOrderResolver readingOrderResolver;
    
    /** initial zone classifier */
    private ZoneClassifier initialClassifier;
    
    /** metadata zone classifier */
    private ZoneClassifier metadataClassifier;

    /** metadata extractor from labelled zones */
    private MetadataExtractor<DocumentMetadata> metadataExtractor;
    
    /** affiliation parser **/
    private ParsableStringParser<DocumentAffiliation> affiliationParser;
    
    /** references strings extractor */
    private BibReferenceExtractor bibReferenceExtractor;
    
    /** bibliographic references parser */
    private BibReferenceParser<BibEntry> bibReferenceParser;
    
    /** content filter */
    private ContentFilter contentFilter;
    
    /** content header extractor */
    private ContentHeadersExtractor contentHeaderExtractor;
    
    /** content header clusterizer */
    private HeadersClusterizer contentHeaderClusterizer;
    
    /** content cleaner */
    private ContentCleaner contentCleaner;
  
    /** citation position finder */
    private ContentCitationPositionFinder citationPositionFinder;
    
   
    boolean timeDebug = false;
    
    public ComponentConfiguration(ContentExtractorConfig configuration) throws AnalysisException {
        try {
            characterExtractor = ComponentFactory.getCharacterExtractor();
            documentSegmenter = ComponentFactory.getDocumentSegmenter();
            readingOrderResolver = ComponentFactory.getReadingOrderResolver();
            initialClassifier = ComponentFactory.getInitialZoneClassifier(
                    configuration.getProperty(ConfigurationProperty.INITIAL_ZONE_CLASSIFIER_MODEL_PATH),
                    configuration.getProperty(ConfigurationProperty.INITIAL_ZONE_CLASSIFIER_RANGE_PATH));
            TimeoutRegister.get().check();
            metadataClassifier = ComponentFactory.getMetadataZoneClassifier(
                    configuration.getProperty(ConfigurationProperty.METADATA_ZONE_CLASSIFIER_MODEL_PATH),
                    configuration.getProperty(ConfigurationProperty.METADATA_ZONE_CLASSIFIER_RANGE_PATH));
            TimeoutRegister.get().check();
            metadataExtractor = ComponentFactory.getMetadataExtractor();
            TimeoutRegister.get().check();
            affiliationParser = ComponentFactory.getAffiliationParser();
            TimeoutRegister.get().check();
            bibReferenceExtractor = ComponentFactory.getBibReferenceExtractor();
            bibReferenceParser = ComponentFactory.getBibReferenceParser();
            contentFilter = ComponentFactory.getContentFilter(
                    configuration.getProperty(ConfigurationProperty.CONTENT_FILTER_MODEL_PATH),
                    configuration.getProperty(ConfigurationProperty.CONTENT_FILTER_RANGE_PATH));
            TimeoutRegister.get().check();
            contentHeaderExtractor = ComponentFactory.getContentHeaderExtractor();
            contentHeaderClusterizer = ComponentFactory.getContentHeaderClusterizer();
            contentCleaner = ComponentFactory.getContentCleaner();
            citationPositionFinder = ComponentFactory.getCitationPositionFinder();
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create ComponentConfiguration!", ex);
        }
    }
    
    public void setCharacterExtractor(CharacterExtractor characterExtractor) {
        this.characterExtractor = characterExtractor;
    }
    
    public void setDocumentSegmenter(DocumentSegmenter documentSegmenter) {
        this.documentSegmenter = documentSegmenter;
    }
    
    public void setReadingOrderResolver(ReadingOrderResolver readingOrderResolver) {
        this.readingOrderResolver = readingOrderResolver;
    }
    
    public void setInitialZoneClassifier(ZoneClassifier initialClassifier) {
        this.initialClassifier = initialClassifier;
    }
    
    public void setInitialZoneClassifier(InputStream model, InputStream range) throws AnalysisException, IOException {
        this.initialClassifier = ComponentFactory.getInitialZoneClassifier(model, range);
    }
    
    public void setMetadataZoneClassifier(ZoneClassifier metadataClassifier) {
        this.metadataClassifier = metadataClassifier;
    }
    
    public void setMetadataZoneClassifier(InputStream model, InputStream range) throws AnalysisException, IOException {
        this.metadataClassifier = ComponentFactory.getMetadataZoneClassifier(model, range);
    }

    public void setMetadataExtractor(MetadataExtractor<DocumentMetadata> metadataExtractor) {
        this.metadataExtractor = metadataExtractor;
    }
    
    public void setAffiliationParser(ParsableStringParser<DocumentAffiliation> affiliationParser) {
        this.affiliationParser = affiliationParser;
    }
    
    public void setBibReferenceExtractor(BibReferenceExtractor bibReferenceExtractor) {
        this.bibReferenceExtractor = bibReferenceExtractor;
    }
    
    public void setBibReferenceParser(BibReferenceParser<BibEntry> bibReferenceParser) {
        this.bibReferenceParser = bibReferenceParser;
    }
    
    public void setBibReferenceParser(InputStream model) throws AnalysisException {
        this.bibReferenceParser = ComponentFactory.getBibReferenceParser(model);
    }
    
    public void setContentCleaner(ContentCleaner contentCleaner) {
        this.contentCleaner = contentCleaner;
    }

    public void setContentFilter(ContentFilter contentFilter) {
        this.contentFilter = contentFilter;
    }

    public void setContentFilter(InputStream model, InputStream range) throws AnalysisException, IOException {
        this.contentFilter = ComponentFactory.getContentFilter(model, range);
    }
    
    public void setContentHeaderExtractor(ContentHeadersExtractor contentHeaderExtractor) {
        this.contentHeaderExtractor = contentHeaderExtractor;
    }
    
    public void setContentHeaderExtractor(InputStream model, InputStream range) throws AnalysisException, IOException {
        this.contentHeaderExtractor = ComponentFactory.getContentHeaderExtractor(model, range);
    }

    public HeadersClusterizer getContentHeaderClusterizer() {
        return contentHeaderClusterizer;
    }

    public void setContentHeaderClusterizer(HeadersClusterizer contentHeaderClusterizer) {
        this.contentHeaderClusterizer = contentHeaderClusterizer;
    }
    
    public ParsableStringParser<DocumentAffiliation> getAffiliationParser() {
        return affiliationParser;
    }

    public BibReferenceExtractor getBibRefExtractor() {
        return bibReferenceExtractor;
    }

    public BibReferenceParser<BibEntry> getBibRefParser() {
        return bibReferenceParser;
    }

    public CharacterExtractor getCharacterExtractor() {
        return characterExtractor;
    }

    public DocumentSegmenter getDocumentSegmenter() {
        return documentSegmenter;
    }

    public ZoneClassifier getInitialClassifier() {
        return initialClassifier;
    }

    public ContentCleaner getContentCleaner() {
        return contentCleaner;
    }

    public ContentFilter getContentFilter() {
        return contentFilter;
    }

    public ContentHeadersExtractor getContentHeaderExtractor() {
        return contentHeaderExtractor;
    }
    
    public ZoneClassifier getMetadataClassifier() {
        return metadataClassifier;
    }

    public MetadataExtractor<DocumentMetadata> getMetadataExtractor() {
        return metadataExtractor;
    }

    public ReadingOrderResolver getReadingOrderResolver() {
        return readingOrderResolver;
    }

    public ContentCitationPositionFinder getCitationPositionFinder() {
        return citationPositionFinder;
    }

    public void setCitationPositionFinder(ContentCitationPositionFinder citationPositionFinder) {
        this.citationPositionFinder = citationPositionFinder;
    }

    public boolean isTimeDebug() {
        return timeDebug;
    }

    public void setTimeDebug(boolean timeDebug) {
        this.timeDebug = timeDebug;
    }
    
}
