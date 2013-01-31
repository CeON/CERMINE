package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import libsvm.svm_parameter;

import org.apache.commons.cli.ParseException;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSampler;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMMetadataClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator {
    @Override
    protected SVMZoneClassifier getZoneClassifier(List<TrainingSample<BxZoneLabel>> trainingSamples) throws IOException, AnalysisException, CloneNotSupportedException {

        Map<BxZoneLabel, BxZoneLabel> labelMapper = BxZoneLabel.getLabelToGeneralMap();
        for (TrainingSample<BxZoneLabel> sample : trainingSamples) {
        	if (sample.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
        		sample.setLabel(labelMapper.get(sample.getLabel()));
            }
        }

        SampleSelector<BxZoneLabel> selector = new OversamplingSampler<BxZoneLabel>(1.0);
        List<TrainingSample<BxZoneLabel>> trainingSamplesOversampled = selector.pickElements(trainingSamples);

        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(SVMMetadataZoneClassifier.getFeatureVectorBuilder());
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.svm_type = svm_parameter.C_SVC;
        param.gamma = 0.5;
        param.C = 256.0;
        param.kernel_type = svm_parameter.RBF;
        zoneClassifier.setParameter(param);
        zoneClassifier.buildClassifier(trainingSamplesOversampled);
//        zoneClassifier.saveModel("svm_meta_classifier");
//        zoneClassifier = new SVMMetadataZoneClassifier();

        return zoneClassifier;
    }

    public static void main(String[] args)
            throws ParseException, AnalysisException, IOException, TransformationException, CloneNotSupportedException {
        CrossvalidatingZoneClassificationEvaluator.main(args, new SVMMetadataClassificationEvaluator());
    }

	@Override
	protected FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder() {
		return SVMMetadataZoneClassifier.getFeatureVectorBuilder();
	}
}
