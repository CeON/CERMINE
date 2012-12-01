package pl.edu.icm.cermine.content.headers;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.knn.KnnClassifier;
import pl.edu.icm.cermine.tools.classification.knn.KnnModel;
import pl.edu.icm.cermine.tools.classification.metrics.FeatureVectorEuclideanMetric;

/**
 *
 * @author Dominika Tkaczyk
 */
public class KnnContentHeaderExtractor extends ContentHeaderExtractor {

    public static final int DEFAULT_KNN_VOTERS = 3;
    /**
     * The maximum number of additional line following the first header line added as a part of the header.
     */
    private int knnVoters = DEFAULT_KNN_VOTERS;
    KnnModel<BxZoneLabel> model;
    FeatureVectorBuilder<BxLine, BxPage> classVectorBuilder;
    KnnClassifier<BxZoneLabel> classifier;

    public KnnContentHeaderExtractor(KnnModel<BxZoneLabel> model, FeatureVectorBuilder<BxLine, BxPage> classVectorBuilder, KnnClassifier<BxZoneLabel> classifier) {
        this.model = model;
        this.classVectorBuilder = classVectorBuilder;
        this.classifier = classifier;
    }

    @Override
    protected boolean isHeader(BxLine line, BxPage page) {
        FeatureVector featureVector = classVectorBuilder.getFeatureVector(line, page);

        BxZoneLabel label = classifier.classify(model, new FeatureVectorEuclideanMetric(), featureVector, knnVoters);
        return label.equals(BxZoneLabel.BODY_HEADER);
    }
}
