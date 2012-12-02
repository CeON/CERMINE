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
    
    protected ContentFilter contentFilter;
    
    protected ContentHeadersExtractor headerExtractor;
    
    protected ContentCleaner contentCleaner;
    
    protected BxContentStructToDocContentStructConverter converter;
    
    
    public DocumentContentStructure extractStructure(BxDocument document) throws AnalysisException, TransformationException {
        document = contentFilter.filter(document);
        BxDocContentStructure tmpContentStructure = headerExtractor.extractHeaders(document);
        contentCleaner.cleanupContent(tmpContentStructure);
        return converter.convert(tmpContentStructure);
    }
    
}
