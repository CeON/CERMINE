package pl.edu.icm.yadda.analysis.articlecontent;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.articlecontent.model.BxDocContentStructure;
import pl.edu.icm.yadda.analysis.articlecontent.model.DocumentContentStructure;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.knn.model.KnnModel;
import pl.edu.icm.yadda.analysis.textr.model.*;

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
            BxDocument document) throws AnalysisException {
        ContentJunkFilter junkFilter = new ContentJunkFilter();
        document = junkFilter.filterJunk(junkFilterModel, junkVectorBuilder, document);
        
        ContentHeaderExtractor headerExtractor = new ContentHeaderExtractor(); 
        BxDocContentStructure tmpContentStructure = headerExtractor.extractHeaders(headerModel, classVectorBuilder, 
                clustVectorBuilder, document);

        ContentCleaner contentCleaner = new ContentCleaner();
        contentCleaner.cleanupContent(tmpContentStructure);
                
        DocumentContentStructure contentStructure = new DocumentContentStructure();
        contentStructure.build(tmpContentStructure);
        
        return contentStructure;
    }
    
}
