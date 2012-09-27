package pl.edu.icm.coansys.metaextr.metadata.evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import libsvm.svm_parameter;

import org.apache.commons.cli.ParseException;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.classification.svm.SVMZoneClassifier;
import pl.edu.icm.coansys.metaextr.metadata.sampleselection.SillyUndersamplingSelector;
import pl.edu.icm.coansys.metaextr.metadata.sampleselection.UndersamplingSelector;
import pl.edu.icm.coansys.metaextr.metadata.sampleselection.OversamplingSelector;
import pl.edu.icm.coansys.metaextr.metadata.sampleselection.SampleSelector;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.coansys.metaextr.textr.ZoneClassifier;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabelCategory;
import pl.edu.icm.coansys.metaextr.textr.tools.InitiallyClassifiedZonesPreprocessor;

public class SVMInitialZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator{ 
	@Override
	protected ZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments)
	{
		FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();
		for(BxDocument doc: trainingDocuments)
			for(BxZone zone: doc.asZones())
				if(zone.getLabel().getGeneralLabel() == BxZoneLabel.GEN_OTHER)
					zone.setLabel(BxZoneLabel.GEN_BODY);
		
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

        double inequalityFactor = 1.5;
        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(1.0);
        trainingElements = selector.pickElements(trainingElements);

        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(featureVectorBuilder);
		svm_parameter param = SVMZoneClassifier.getDefaultParam();
		param.svm_type = svm_parameter.C_SVC;
		param.gamma = 0.0625;
		param.C = 2.82842712475;
		param.degree = 4;
		param.kernel_type = svm_parameter.POLY;

        zoneClassifier.buildClassifier(trainingElements);
		zoneClassifier.setParameter(param);
//        zoneClassifier.loadModel(modelPath, rangeFilePath);
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
			if(zone.getLabel().getGeneralLabel() == BxZoneLabel.GEN_OTHER)
				zone.setLabel(BxZoneLabel.GEN_BODY);
			else
				zone.setLabel(zone.getLabel().getGeneralLabel());
		}
	}
}
