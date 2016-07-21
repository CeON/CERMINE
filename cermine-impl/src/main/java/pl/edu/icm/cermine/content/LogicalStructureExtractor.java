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

package pl.edu.icm.cermine.content;

import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.filtering.ContentFilter;
import pl.edu.icm.cermine.content.headers.ContentHeadersExtractor;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.transformers.BxContentToDocContentConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class LogicalStructureExtractor {
    
    private ContentFilter contentFilter;
    
    private ContentHeadersExtractor headerExtractor;
    
    private ContentCleaner contentCleaner;
    
    private BxContentToDocContentConverter converter;

    public LogicalStructureExtractor() {
    }
    
    public LogicalStructureExtractor(ContentFilter contentFilter, ContentHeadersExtractor headerExtractor, 
            ContentCleaner contentCleaner, BxContentToDocContentConverter converter) {
        this.contentFilter = contentFilter;
        this.headerExtractor = headerExtractor;
        this.contentCleaner = contentCleaner;
        this.converter = converter;
    }
    
    
    public ContentStructure extractStructure(BxDocument document) throws AnalysisException {
        try {
            BxDocument doc = contentFilter.filter(document);
            BxContentStructure tmpContentStructure = headerExtractor.extractHeaders(doc);
            contentCleaner.cleanupContent(tmpContentStructure);
            return converter.convert(tmpContentStructure);
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract logical structure!", ex);
        }
    }

    public ContentCleaner getContentCleaner() {
        return contentCleaner;
    }

    public void setContentCleaner(ContentCleaner contentCleaner) {
        this.contentCleaner = contentCleaner;
    }

    public ContentFilter getContentFilter() {
        return contentFilter;
    }

    public void setContentFilter(ContentFilter contentFilter) {
        this.contentFilter = contentFilter;
    }

    public BxContentToDocContentConverter getConverter() {
        return converter;
    }

    public void setConverter(BxContentToDocContentConverter converter) {
        this.converter = converter;
    }

    public ContentHeadersExtractor getHeaderExtractor() {
        return headerExtractor;
    }

    public void setHeaderExtractor(ContentHeadersExtractor headerExtractor) {
        this.headerExtractor = headerExtractor;
    }
    
}
