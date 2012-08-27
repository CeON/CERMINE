package pl.edu.icm.yadda.analysis.articlecontent;

import java.util.List;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.knn.classifier.KnnClassifier;
import pl.edu.icm.yadda.analysis.classification.knn.model.KnnModel;
import pl.edu.icm.yadda.analysis.classification.knn.model.KnnTrainingSample;
import pl.edu.icm.yadda.analysis.classification.metrics.FeatureVectorEuclideanMetric;
import pl.edu.icm.yadda.analysis.textr.model.*;

/**
 *
 * @author Dominika Tkaczyk
 */
public class ContentJunkFilter {

    /**
     * The number of nearest training samples used for line classification.
     */
    private int knnVoters = 3;
    
        
    public BxDocument filterJunk(KnnModel<BxZoneLabel> model, FeatureVectorBuilder<BxZone, BxPage> vectorBuilder, 
            BxDocument document) throws AnalysisException {
        KnnClassifier<BxZoneLabel> classifier = new KnnClassifier<BxZoneLabel>();
        
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)) {
                    FeatureVector featureVector = vectorBuilder.getFeatureVector(zone, page);
                    BxZoneLabel label = classifier.classify(model, new FeatureVectorEuclideanMetric(), featureVector, knnVoters);
                    zone.setLabel(label);
                }
            }
        }

        return document;
    }

    public KnnModel<BxZoneLabel> buildModel(FeatureVectorBuilder<BxZone, BxPage> vectorBuilder,
            List<BxDocument> documents) {
        KnnModel<BxZoneLabel> model = new KnnModel<BxZoneLabel>();
      
        for (BxDocument document : documents) {
            for (BxPage page : document.getPages()) {
                for (BxZone zone : page.getZones()) {
                    if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)) {
                        FeatureVector fv = vectorBuilder.getFeatureVector(zone, page);
                        if (zone.getLabel().equals(BxZoneLabel.BODY_JUNK) 
                                || zone.getLabel().equals(BxZoneLabel.BODY_EQUATION)
                                || zone.getLabel().equals(BxZoneLabel.BODY_EQUATION_LABEL)
                                || zone.getLabel().equals(BxZoneLabel.BODY_FIGURE)
                                || zone.getLabel().equals(BxZoneLabel.BODY_FIGURE_CAPTION)
                                || zone.getLabel().equals(BxZoneLabel.BODY_TABLE)
                                || zone.getLabel().equals(BxZoneLabel.BODY_TABLE_CAPTION)) {
                            model.addTrainingSample(new KnnTrainingSample(fv, BxZoneLabel.BODY_JUNK));
                        } else {
                            model.addTrainingSample(new KnnTrainingSample(fv, BxZoneLabel.BODY_CONTENT));
                        }
                    }
                }
            }
        }
        
        return model;
    }

}
