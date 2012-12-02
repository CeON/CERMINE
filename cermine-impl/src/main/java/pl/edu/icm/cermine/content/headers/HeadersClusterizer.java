package pl.edu.icm.cermine.content.headers;

import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.tools.classification.clustering.Clusterizer;
import pl.edu.icm.cermine.tools.classification.clustering.FeatureVectorClusterizer;
import pl.edu.icm.cermine.tools.classification.clustering.SingleLinkageClusterizer;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.metrics.FeatureVectorDistanceMetric;
import pl.edu.icm.cermine.tools.classification.metrics.FeatureVectorEuclideanMetric;

/**
 *
 * @author Dominika Tkaczyk
 */
public class HeadersClusterizer {
    
    public static final double DEFAULT_MAX_HEADER_LEV_DIST = 1;
    
    private double maxHeaderLevelDistance = DEFAULT_MAX_HEADER_LEV_DIST;
    
    private FeatureVectorBuilder<BxLine, BxPage> vectorBuilder;
    
    private Clusterizer clusterizer;
    
    private FeatureVectorDistanceMetric metric;

    public HeadersClusterizer() {
        this.vectorBuilder = HeaderExtractingTools.CLUSTERING_VB;
        this.clusterizer = new SingleLinkageClusterizer();
        this.metric = new FeatureVectorEuclideanMetric();
    }
    
    public HeadersClusterizer(FeatureVectorBuilder<BxLine, BxPage> vectorBuilder, Clusterizer clusterizer, FeatureVectorDistanceMetric metric) {
        this.vectorBuilder = vectorBuilder;
        this.clusterizer = clusterizer;
        this.metric = metric;
    }
    
    
    public void clusterHeaders(BxDocContentStructure contentStructure) {
        FeatureVectorClusterizer fvClusterizer = new FeatureVectorClusterizer();
        fvClusterizer.setClusterizer(clusterizer);
        int[] clusters = fvClusterizer.clusterize(contentStructure.getFirstHeaderFeatureVectors(vectorBuilder), 
                vectorBuilder, metric, maxHeaderLevelDistance, true);
        contentStructure.setHeaderLevelIds(clusters);
    }

    public void setClusterizer(Clusterizer clusterizer) {
        this.clusterizer = clusterizer;
    }

    public void setMaxHeaderLevelDistance(double maxHeaderLevelDistance) {
        this.maxHeaderLevelDistance = maxHeaderLevelDistance;
    }

    public void setMetric(FeatureVectorDistanceMetric metric) {
        this.metric = metric;
    }

    public void setVectorBuilder(FeatureVectorBuilder<BxLine, BxPage> vectorBuilder) {
        this.vectorBuilder = vectorBuilder;
    }
    
}
