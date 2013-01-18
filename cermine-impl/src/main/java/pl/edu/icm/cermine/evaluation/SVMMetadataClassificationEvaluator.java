package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import libsvm.svm_parameter;
import org.apache.commons.cli.ParseException;

import pl.edu.icm.cermine.evaluation.tools.ClassificationResults;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.*;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.ClassificationUtils;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMMetadataClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator {
    @Override
    protected SVMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments) throws IOException, AnalysisException {
        FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = SVMMetadataZoneClassifier.getFeatureVectorBuilder();

        Map<BxZoneLabel, BxZoneLabel> labelMapper = BxZoneLabel.getLabelToGeneralMap();
        for (BxDocument doc : trainingDocuments) {
            for (BxZone zone : doc.asZones()) {
                if (zone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
                    zone.setLabel(labelMapper.get(zone.getLabel()));
                }
            }
        }

        List<TrainingSample<BxZoneLabel>> trainingSamplesUnrevised = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(trainingDocuments, featureVectorBuilder);
        trainingSamplesUnrevised = ClassificationUtils.filterElements(trainingSamplesUnrevised, BxZoneLabelCategory.CAT_METADATA);

        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(0.7);
        List<TrainingSample<BxZoneLabel>> trainingSamples = selector.pickElements(trainingSamplesUnrevised);


        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(featureVectorBuilder);
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.svm_type = svm_parameter.C_SVC;
        param.gamma = 1.0 / 2.0;
        param.C = 256.0;
        //param.degree = 3;
        param.kernel_type = svm_parameter.RBF;
        zoneClassifier.setParameter(param);

        zoneClassifier.buildClassifier(trainingSamples);
        zoneClassifier.saveModel("metadata_classifier");
        zoneClassifier.printWeigths(featureVectorBuilder);

        return zoneClassifier;
    }

    public static void main(String[] args)
            throws ParseException, AnalysisException, IOException, TransformationException {
        CrossvalidatingZoneClassificationEvaluator.main(args, new SVMMetadataClassificationEvaluator());
    }

    @Override
    protected ClassificationResults compareDocuments(BxDocument expected, BxDocument actual) {
        assert expected.asZones().size() == actual.asZones().size();

        ClassificationResults ret = newResults();
        for (Integer idx = 0; idx < expected.asZones().size(); ++idx) {
            BxZone expectedZone = expected.asZones().get(idx);
            BxZone actualZone = actual.asZones().get(idx);
            if (expectedZone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
                continue;
            }

            //System.out.println("--- " + actualZone.getLabel() + " " + expectedZone.getLabel());
            ret.add(compareItems(expectedZone, actualZone));
        }
        return ret;
    }

    @Override
    protected void preprocessDocumentForEvaluation(BxDocument doc) {
    }
}
