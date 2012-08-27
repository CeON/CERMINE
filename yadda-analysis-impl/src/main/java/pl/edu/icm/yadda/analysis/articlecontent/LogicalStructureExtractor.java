package pl.edu.icm.yadda.analysis.articlecontent;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.articlecontent.model.BxDocContentStructure;
import pl.edu.icm.yadda.analysis.articlecontent.model.DocumentContentStructure;
import pl.edu.icm.yadda.analysis.classification.clustering.FeatureVectorClusterizer;
import pl.edu.icm.yadda.analysis.classification.clustering.SingleLinkageClusterizer;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.knn.model.KnnModel;
import pl.edu.icm.yadda.analysis.classification.metrics.FeatureVectorEuclideanMetric;
import pl.edu.icm.yadda.analysis.textr.model.*;

/**
 *
 * @author Dominika Tkaczyk
 */
public class LogicalStructureExtractor {

    private double maxHeaderLevelDistance = 1;
    
    
    public DocumentContentStructure extractStructure(KnnModel<BxZoneLabel> junkFilterModel, 
            KnnModel<BxZoneLabel> headerModel,
            FeatureVectorBuilder<BxZone, BxPage> junkVectorBuilder, 
            FeatureVectorBuilder<BxLine, BxPage> classVectorBuilder, 
            FeatureVectorBuilder<BxLine, BxPage> clustVectorBuilder, 
            BxDocument document) throws AnalysisException {
        ContentJunkFilter junkFilter = new ContentJunkFilter();
        document = junkFilter.filterJunk(junkFilterModel, junkVectorBuilder, document);
        
        ContentHeaderMarker headerMarker = new ContentHeaderMarker(); 
        BxDocContentStructure tmpContentStructure = headerMarker.extractHeaders(headerModel, classVectorBuilder, document);

        FeatureVectorClusterizer clusterizer = new FeatureVectorClusterizer();
        clusterizer.setClusterizer(new SingleLinkageClusterizer());
        int[] clusters = clusterizer.clusterize(tmpContentStructure.getFirstHeaderFeatureVectors(clustVectorBuilder), 
                clustVectorBuilder, new FeatureVectorEuclideanMetric(), maxHeaderLevelDistance, true);
        tmpContentStructure.setHeaderLevelIds(clusters);

        ContentCleaner contentCleaner = new ContentCleaner();
        contentCleaner.cleanupContent(tmpContentStructure);
                
        DocumentContentStructure contentStructure = new DocumentContentStructure();
        contentStructure.build(tmpContentStructure);
        
        return contentStructure;
    }
    
}
