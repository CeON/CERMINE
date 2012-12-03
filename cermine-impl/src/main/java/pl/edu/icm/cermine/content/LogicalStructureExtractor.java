package pl.edu.icm.cermine.content;

import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.filtering.ContentFilter;
import pl.edu.icm.cermine.content.headers.ContentHeadersExtractor;
import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.content.transformers.BxContentStructToDocContentStructConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 *
 * @author Dominika Tkaczyk
 */
public abstract class LogicalStructureExtractor {
    
    private ContentFilter contentFilter;
    
    private ContentHeadersExtractor headerExtractor;
    
    private ContentCleaner contentCleaner;
    
    private BxContentStructToDocContentStructConverter converter;

    public LogicalStructureExtractor(ContentFilter contentFilter, ContentHeadersExtractor headerExtractor, ContentCleaner contentCleaner, BxContentStructToDocContentStructConverter converter) {
        this.contentFilter = contentFilter;
        this.headerExtractor = headerExtractor;
        this.contentCleaner = contentCleaner;
        this.converter = converter;
    }
    
    
    public DocumentContentStructure extractStructure(BxDocument document) throws AnalysisException, TransformationException {
        BxDocument doc = contentFilter.filter(document);
        BxDocContentStructure tmpContentStructure = headerExtractor.extractHeaders(doc);
        contentCleaner.cleanupContent(tmpContentStructure);
        return converter.convert(tmpContentStructure);
    }
    
}
