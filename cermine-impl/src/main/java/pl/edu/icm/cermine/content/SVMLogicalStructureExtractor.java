package pl.edu.icm.cermine.content;

import java.io.BufferedReader;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.filtering.SVMContentFilter;
import pl.edu.icm.cermine.content.headers.SVMContentHeadersExtractor;
import pl.edu.icm.cermine.content.transformers.BxContentStructToDocContentStructConverter;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class SVMLogicalStructureExtractor extends LogicalStructureExtractor {
    
    public SVMLogicalStructureExtractor(BufferedReader filterModelFile, BufferedReader filterRangeFile,
            BufferedReader headerModelFile, BufferedReader headerRangeFile) throws AnalysisException {
        this.contentFilter = new SVMContentFilter(filterModelFile, filterRangeFile);
        this.headerExtractor = new SVMContentHeadersExtractor(headerModelFile, headerRangeFile);
        this.contentCleaner = new ContentCleaner();
        this.converter = new BxContentStructToDocContentStructConverter();
    }
    
}
