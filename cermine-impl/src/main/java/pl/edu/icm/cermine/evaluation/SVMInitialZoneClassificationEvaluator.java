package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.List;
import libsvm.svm_parameter;
import org.apache.commons.cli.ParseException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMInitialZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator{ 
	@Override
	protected ZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments) throws IOException
	{
		FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();
//		for(BxDocument doc: trainingDocuments)
//			for(BxZone zone: doc.asZones())
//				if(zone.getLabel().getGeneralLabel() == BxZoneLabel.GEN_OTHER)
//					zone.setLabel(BxZoneLabel.GEN_BODY);
		
        BxDocsToHMMConverter node = new BxDocsToHMMConverter(featureVectorBuilder, BxZoneLabel.getLabelToGeneralMap());
        
        List<TrainingElement<BxZoneLabel>> trainingElements;
        try {
        	trainingElements = node.process(trainingDocuments);
        } catch(Exception e) {
        	System.out.println(e.getCause());
        	e.printStackTrace();
			throw new RuntimeException("Unable to process the delivered training documents!");
		}

        // Filter the training documents
        // so that in the learning examples all classes are
        // represented equally

        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(1.0);
        trainingElements = selector.pickElements(trainingElements);

        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(featureVectorBuilder);
		svm_parameter param = SVMZoneClassifier.getDefaultParam();
		param.svm_type = svm_parameter.C_SVC;
		param.gamma = 0.0625;
		param.C = 2.82842712475;
		param.degree = 4;
		param.kernel_type = svm_parameter.POLY;

		zoneClassifier.setParameter(param);
        zoneClassifier.buildClassifier(trainingElements);
        zoneClassifier.printWeigths(featureVectorBuilder);
		return zoneClassifier;
	}

	public static void main(String[] args) 
			throws ParseException, RuntimeException, AnalysisException, IOException {
		CrossvalidatingZoneClassificationEvaluator.main(args, new SVMInitialZoneClassificationEvaluator());
	}

	@Override
	protected ClassificationResults compareDocuments(BxDocument expected, BxDocument actual) {
		ClassificationResults ret = newResults();
		for(Integer idx=0; idx < actual.asZones().size(); ++idx) {
			ClassificationResults itemResults = compareItems(expected.asZones().get(idx), actual.asZones().get(idx));
			ret.add(itemResults);
		}
		return ret;
	}

	@Override
	protected void preprocessDocumentForEvaluation(BxDocument doc) {
		for(BxZone zone: doc.asZones()) {
//			if(zone.getLabel().getGeneralLabel() == BxZoneLabel.GEN_OTHER)
//				zone.setLabel(BxZoneLabel.GEN_BODY);
//			else
				zone.setLabel(zone.getLabel().getGeneralLabel());
		}
	}
}
