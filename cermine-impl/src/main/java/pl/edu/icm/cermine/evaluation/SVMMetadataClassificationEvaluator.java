package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import libsvm.svm_parameter;
import org.apache.commons.cli.ParseException;
import pl.edu.icm.cermine.evaluation.tools.ClassificationResults;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.SVMInitialZoneClassifier;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.ClassificationUtils;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMMetadataClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator {
    @Override
    protected SVMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments) throws IOException, AnalysisException {

        Map<BxZoneLabel, BxZoneLabel> labelMapper = BxZoneLabel.getLabelToGeneralMap();
        for (BxDocument doc : trainingDocuments) {
            for (BxZone zone : doc.asZones()) {
                if (zone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
                    zone.setLabel(labelMapper.get(zone.getLabel()));
                }
            }
        }

        List<TrainingSample<BxZoneLabel>> trainingSamplesUnrevised = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(trainingDocuments, SVMMetadataZoneClassifier.getFeatureVectorBuilder());
        trainingSamplesUnrevised = ClassificationUtils.filterElements(trainingSamplesUnrevised, BxZoneLabelCategory.CAT_METADATA);

        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(0.7);
        List<TrainingSample<BxZoneLabel>> trainingSamples = selector.pickElements(trainingSamplesUnrevised);


        SVMZoneClassifier zoneClassifier = new SVMInitialZoneClassifier();
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.svm_type = svm_parameter.C_SVC;
        param.gamma = 0.25;
        param.C = 32.0;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 4;
        zoneClassifier.setParameter(param);
        zoneClassifier.buildClassifier(trainingSamples);

//        zoneClassifier.saveModel("svm_meta_classifier");
//        zoneClassifier = new SVMMetadataZoneClassifier();
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
