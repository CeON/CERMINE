package pl.edu.icm.cermine.content;

import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.filtering.KnnContentFilter;
import pl.edu.icm.cermine.content.headers.KnnContentHeadersExtractor;
import pl.edu.icm.cermine.content.transformers.BxContentStructToDocContentStructConverter;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.knn.KnnClassifier;
import pl.edu.icm.cermine.tools.classification.knn.KnnModel;

/**
 *
 * @author Dominika Tkaczyk
 */
public class KnnLogicalStructureExtractor extends LogicalStructureExtractor {

    public KnnLogicalStructureExtractor(KnnModel<BxZoneLabel> knnFilterModel, KnnModel<BxZoneLabel> knnHeaderModel) {
        super (new KnnContentFilter(knnFilterModel), 
                new KnnContentHeadersExtractor(knnHeaderModel, new KnnClassifier<BxZoneLabel>()),
                new ContentCleaner(), 
                new BxContentStructToDocContentStructConverter());
    }
    
}
