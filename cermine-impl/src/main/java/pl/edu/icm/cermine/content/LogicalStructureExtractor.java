package pl.edu.icm.cermine.content;

import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.filtering.KnnContentFilter;
import pl.edu.icm.cermine.content.headers.ContentHeaderExtractor;
import pl.edu.icm.cermine.content.headers.KnnContentHeaderExtractor;
import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.content.transformers.BxContentStructToDocContentStructConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.knn.KnnClassifier;
import pl.edu.icm.cermine.tools.classification.knn.KnnModel;

/**
 *
 * @author Dominika Tkaczyk
 */
public class LogicalStructureExtractor {
    
    public DocumentContentStructure extractStructure(KnnModel<BxZoneLabel> junkFilterModel, 
            KnnModel<BxZoneLabel> headerModel,
            FeatureVectorBuilder<BxZone, BxPage> junkVectorBuilder, 
            FeatureVectorBuilder<BxLine, BxPage> classVectorBuilder, 
            FeatureVectorBuilder<BxLine, BxPage> clustVectorBuilder, 
            BxDocument document) throws AnalysisException, TransformationException {
        KnnContentFilter junkFilter = new KnnContentFilter(junkFilterModel, junkVectorBuilder);
        document = junkFilter.filter(document);
        
        ContentHeaderExtractor headerExtractor = new KnnContentHeaderExtractor(headerModel, classVectorBuilder, new KnnClassifier<BxZoneLabel>()); 
        BxDocContentStructure tmpContentStructure = headerExtractor.extractHeaders( 
                clustVectorBuilder, document);

        ContentCleaner contentCleaner = new ContentCleaner();
        contentCleaner.cleanupContent(tmpContentStructure);
        
        BxContentStructToDocContentStructConverter converter = new BxContentStructToDocContentStructConverter();
                
        DocumentContentStructure contentStructure = converter.convert(tmpContentStructure);
        
        return contentStructure;
    }
    
}
