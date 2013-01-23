package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.List;

import libsvm.svm_parameter;

import org.apache.commons.cli.ParseException;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.SVMInitialZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMInitialZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator {

    @Override
    protected SVMZoneClassifier getZoneClassifier(List<TrainingSample<BxZoneLabel>> trainingSamples) throws IOException, AnalysisException {
        for (TrainingSample<BxZoneLabel> trainingSample : trainingSamples) {
            trainingSample.setLabel(trainingSample.getLabel().getGeneralLabel());
        }

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
}

