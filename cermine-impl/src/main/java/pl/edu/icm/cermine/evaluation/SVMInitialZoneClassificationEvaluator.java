package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.List;

import libsvm.svm_parameter;

import org.apache.commons.cli.ParseException;

import pl.edu.icm.cermine.evaluation.tools.ClassificationResults;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.SVMInitialZoneClassifier;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMInitialZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator {

    @Override
    protected ZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments) throws IOException, AnalysisException {
        for (BxDocument doc : trainingDocuments) {
            for (BxZone zone : doc.asZones()) {
                zone.setLabel(zone.getLabel().getGeneralLabel());
            }
        }

        List<TrainingSample<BxZoneLabel>> trainingSamples;
        trainingSamples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(trainingDocuments, SVMInitialZoneClassifier.getFeatureVectorBuilder(),
                BxZoneLabel.getLabelToGeneralMap());

        SVMZoneClassifier zoneClassifier = new SVMInitialZoneClassifier();
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.svm_type = svm_parameter.C_SVC;
        param.gamma = 0.126;
        param.C = 64.0;
        param.kernel_type = svm_parameter.RBF;
        zoneClassifier.setParameter(param);
        zoneClassifier.buildClassifier(trainingSamples);
        return zoneClassifier;
    }

	public static void main(String[] args) 
			throws ParseException, AnalysisException, IOException, TransformationException {
		CrossvalidatingZoneClassificationEvaluator.main(args, new SVMInitialZoneClassificationEvaluator());
	}

    @Override
    protected ClassificationResults compareDocuments(BxDocument expected, BxDocument actual) {
        ClassificationResults ret = newResults();
        for (Integer idx = 0; idx < actual.asZones().size(); ++idx) {
            ClassificationResults itemResults = compareItems(expected.asZones().get(idx), actual.asZones().get(idx));
            ret.add(itemResults);
        }
        return ret;
    }

    @Override
    protected void preprocessDocumentForEvaluation(BxDocument doc) {
        for (BxZone zone : doc.asZones()) {
            zone.setLabel(zone.getLabel().getGeneralLabel());
        }
    }
}
