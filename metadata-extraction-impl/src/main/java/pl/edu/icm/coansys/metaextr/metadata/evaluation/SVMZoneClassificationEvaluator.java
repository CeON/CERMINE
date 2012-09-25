package pl.edu.icm.coansys.metaextr.metadata.evaluation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import libsvm.svm_parameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections.iterators.ArrayIterator;


import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.classification.svm.SVMZoneClassifier;
import pl.edu.icm.coansys.metaextr.metadata.sampleselection.SillyUndersamplingSelector;
import pl.edu.icm.coansys.metaextr.metadata.sampleselection.UndersamplingSelector;
import pl.edu.icm.coansys.metaextr.metadata.sampleselection.OversamplingSelector;
import pl.edu.icm.coansys.metaextr.metadata.sampleselection.SampleSelector;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabelCategory;
import pl.edu.icm.coansys.metaextr.textr.tools.InitiallyClassifiedZonesPreprocessor;

public class SVMZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator{ 
	@Override
	protected SVMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments)
	{
		FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();
        BxDocsToHMMConverter node = new BxDocsToHMMConverter();
        node.setFeatureVectorBuilder(featureVectorBuilder);
        node.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
        
        List<TrainingElement<BxZoneLabel>> trainingElementsUnrevised;
        try {
        	trainingElementsUnrevised = node.process(trainingDocuments);
        } catch(Exception e) {
        	System.out.println(e.getCause());
        	e.printStackTrace();
			throw new RuntimeException("Unable to process the delivered training documents!");
		}

        // Filter the training documents
        // so that in the learning examples all classes are
        // represented equally

        double inequalityFactor = 1.5;
//        SampleSelector<BxZoneLabel> selector = new UndersamplingSelector<BxZoneLabel>(inequalityFactor);
        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(1.0);
//        SampleSelector<BxZoneLabel, HMMTrainingElement<BxZoneLabel>> selector =
//        		new SillyUndersamplingSelector<BxZoneLabel, HMMTrainingElement<BxZoneLabel>>(BxZoneLabel.valuesOfCategory(inequalityFactor);
        List<TrainingElement<BxZoneLabel>> trainingElements = selector.pickElements(trainingElementsUnrevised);

        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(featureVectorBuilder);
		svm_parameter param = SVMZoneClassifier.getDefaultParam();
		param.svm_type = svm_parameter.C_SVC;
//		param.gamma = 32;
//		param.C = 4.0;
//		param.degree = 3;
		param.gamma = 0.176776695297;
		param.C = 4.0;
		param.degree = 2;
		param.kernel_type = svm_parameter.POLY;
		zoneClassifier.setParameter(param);

        zoneClassifier.buildClassifier(trainingElements);
        zoneClassifier.printWeigths(featureVectorBuilder);
        
        Set<String> fnames = featureVectorBuilder.getFeatureNames();
        assert fnames.size() == zoneClassifier.getWeights().length;

		return zoneClassifier;
	}

	@Override
	protected SampleSelector<BxZoneLabel> getSampleFilter() {
		return new SampleSelector<BxZoneLabel>() {
			@Override
			public List<TrainingElement<BxZoneLabel>> pickElements(List<TrainingElement<BxZoneLabel>> inputElements) {
				List<TrainingElement<BxZoneLabel>> ret = new ArrayList<TrainingElement<BxZoneLabel>>();
				ret.addAll(inputElements);
				return ret;
			}
		};
	}

	public static void main(String[] args) throws ParseException {
		CrossvalidatingZoneClassificationEvaluator.main(args, new SVMZoneClassificationEvaluator());
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
		for(BxZone zone: doc.asZones())
			zone.setLabel(zone.getLabel().getGeneralLabel());
	}
}
