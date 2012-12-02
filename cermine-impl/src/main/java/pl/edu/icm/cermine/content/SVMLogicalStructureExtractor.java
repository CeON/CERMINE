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
        super(new SVMContentFilter(filterModelFile, filterRangeFile),
                new SVMContentHeadersExtractor(headerModelFile, headerRangeFile),
                new ContentCleaner(),
                new BxContentStructToDocContentStructConverter());
    }
    
}
