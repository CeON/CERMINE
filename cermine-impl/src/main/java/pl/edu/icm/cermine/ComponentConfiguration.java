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
import pl.edu.icm.cermine.bibref.BibReferenceExtractor;
import pl.edu.icm.cermine.bibref.BibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.sentiment.CitationContextFinder;
import pl.edu.icm.cermine.bibref.sentiment.CitationPositionFinder;
import pl.edu.icm.cermine.bibref.sentiment.CitationSentimentAnalyser;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.filtering.ContentFilter;
import pl.edu.icm.cermine.content.headers.ContentHeadersExtractor;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.MetadataExtractor;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.parsing.tools.ParsableStringParser;
import pl.edu.icm.cermine.structure.CharacterExtractor;
import pl.edu.icm.cermine.structure.DocumentSegmenter;
import pl.edu.icm.cermine.structure.ReadingOrderResolver;
import pl.edu.icm.cermine.structure.ZoneClassifier;

/**
 * The class represents the configuration of the extraction system.
 * It contains all the objects performing the individual steps
 * of an extraction task.
 * 
 * @author Dominika Tkaczyk
 */
public class ComponentConfiguration {

    /** character extractor */
    CharacterExtractor characterExtractor;
    
    /** document object segmenter */
    DocumentSegmenter documentSegmenter;
    
    /** reading order resolver */
    ReadingOrderResolver readingOrderResolver;
    
    /** initial zone classifier */
    ZoneClassifier initialClassifier;
    
    /** metadata zone classifier */
    ZoneClassifier metadataClassifier;

    /** metadata extractor from labelled zones */
    MetadataExtractor<DocumentMetadata> metadataExtractor;
    
    /** affiliation parser **/
    ParsableStringParser<DocumentAffiliation> affiliationParser;
    
    /** references strings extractor */
    BibReferenceExtractor bibReferenceExtractor;
    
    /** bibliographic references parser */
    BibReferenceParser<BibEntry> bibReferenceParser;
    
    /** content filter */
    ContentFilter contentFilter;
    
    /** content header extractor */
    ContentHeadersExtractor contentHeaderExtractor;
    
    /** content cleaner */
    ContentCleaner contentCleaner;
  
    /** citation position finder */
    CitationPositionFinder citationPositionFinder;
    
    /** citation context finder */
    CitationContextFinder citationContextFinder;
    
    /** citation sentiment analyser */
    CitationSentimentAnalyser citationSentimentAnalyser;
    
   
    public ComponentConfiguration() throws AnalysisException {
        try {
            characterExtractor = ComponentFactory.getCharacterExtractor();
            documentSegmenter = ComponentFactory.getDocumentSegmenter();
            readingOrderResolver = ComponentFactory.getReadingOrderResolver();
            initialClassifier = ComponentFactory.getInitialZoneClassifier();
            metadataClassifier = ComponentFactory.getMetadataZoneClassifier();
            metadataExtractor = ComponentFactory.getMetadataExtractor();
            affiliationParser = ComponentFactory.getAffiliationParser();
            bibReferenceExtractor = ComponentFactory.getBibReferenceExtractor();
            bibReferenceParser = ComponentFactory.getBibReferenceParser(); 
            contentFilter = ComponentFactory.getContentFilter();
            contentHeaderExtractor = ComponentFactory.getContentHeaderExtractor();
            contentCleaner = ComponentFactory.getContentCleaner();
            citationPositionFinder = ComponentFactory.getCitationPositionFinder();
            citationContextFinder = ComponentFactory.getCitationContextFinder();
            citationSentimentAnalyser = ComponentFactory.getCitationSentimentAnalyser();
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

    public CitationContextFinder getCitationContextFinder() {
        return citationContextFinder;
    }

    public void setCitationContextFinder(CitationContextFinder citationContextFinder) {
        this.citationContextFinder = citationContextFinder;
    }

    public CitationPositionFinder getCitationPositionFinder() {
        return citationPositionFinder;
    }

    public void setCitationPositionFinder(CitationPositionFinder citationPositionFinder) {
        this.citationPositionFinder = citationPositionFinder;
    }

    public CitationSentimentAnalyser getCitationSentimentAnalyser() {
        return citationSentimentAnalyser;
    }

    public void setCitationSentimentAnalyser(CitationSentimentAnalyser citationSentimentAnalyser) {
        this.citationSentimentAnalyser = citationSentimentAnalyser;
    }
    
}
