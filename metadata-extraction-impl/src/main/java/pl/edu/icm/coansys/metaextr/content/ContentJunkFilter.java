package pl.edu.icm.coansys.metaextr.content;

import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabelCategory;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import java.util.List;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.knn.KnnClassifier;
import pl.edu.icm.coansys.metaextr.tools.classification.knn.KnnModel;
import pl.edu.icm.coansys.metaextr.tools.classification.knn.KnnTrainingSample;
import pl.edu.icm.coansys.metaextr.tools.classification.metrics.FeatureVectorEuclideanMetric;

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
