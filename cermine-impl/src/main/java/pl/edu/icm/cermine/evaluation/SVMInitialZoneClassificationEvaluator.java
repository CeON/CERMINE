package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.List;

import libsvm.svm_parameter;

import org.apache.commons.cli.ParseException;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.SVMInitialZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSampler;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMInitialZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator {

    @Override
    protected SVMZoneClassifier getZoneClassifier(List<TrainingSample<BxZoneLabel>> trainingSamples) throws IOException, AnalysisException, CloneNotSupportedException {
        for (TrainingSample<BxZoneLabel> trainingSample : trainingSamples) {
            trainingSample.setLabel(trainingSample.getLabel().getGeneralLabel());
        }

		OversamplingSampler<BxZoneLabel> selector = new OversamplingSampler<BxZoneLabel>(
				1.);
		List<TrainingSample<BxZoneLabel>> trainingSamplesOversampled = selector
				.pickElements(trainingSamples);
        
        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(SVMInitialZoneClassifier.getFeatureVectorBuilder());
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.svm_type = svm_parameter.C_SVC;
		param.gamma = 0.5;
        param.C = 64.0;
        param.kernel_type = svm_parameter.RBF;
        zoneClassifier.setParameter(param);
        zoneClassifier.buildClassifier(trainingSamplesOversampled);
//        zoneClassifier.saveModel("svm_initial_classifier");
        return zoneClassifier;
    }

	public static void main(String[] args) 
			throws ParseException, AnalysisException, IOException, TransformationException, CloneNotSupportedException {
		CrossvalidatingZoneClassificationEvaluator.main(args, new SVMInitialZoneClassificationEvaluator());
	}

	@Override
	protected FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder() {
		return SVMInitialZoneClassifier.getFeatureVectorBuilder();
	}
}

