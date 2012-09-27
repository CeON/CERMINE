package pl.edu.icm.coansys.metaextr.metadata.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import libsvm.svm_parameter;

import org.apache.commons.cli.ParseException;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.classification.svm.SVMZoneClassifier;
import pl.edu.icm.coansys.metaextr.classification.tools.PipelineClassifier;
import pl.edu.icm.coansys.metaextr.classification.tools.PipelineClassifier.PickyClassifier;
import pl.edu.icm.coansys.metaextr.metadata.sampleselection.OversamplingSelector;
import pl.edu.icm.coansys.metaextr.metadata.sampleselection.SampleSelector;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.coansys.metaextr.textr.ZoneClassifier;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabelCategory;
import pl.edu.icm.coansys.metaextr.textr.tools.BxModelUtils;

public class SVMZoneClassificationEvaluator extends
		CrossvalidatingZoneClassificationEvaluator {

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
			if(zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_OTHER)
				zone.setLabel(BxZoneLabel.GEN_OTHER);
			else if(zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_BODY)
				zone.setLabel(BxZoneLabel.GEN_BODY);
			else if(zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_REFERENCES)
				zone.setLabel(BxZoneLabel.GEN_REFERENCES);
	}

	@Override
	protected ZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments) throws AnalysisException {
		FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();
        BxDocsToHMMConverter node = new BxDocsToHMMConverter(featureVectorBuilder, BxZoneLabel.getLabelToGeneralMap());
        
        // Filter the training documents
        // so that in the learning examples all classes are
        // represented equally

        double inequalityFactor = 1.5;
        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(1.0);
        List<TrainingElement<BxZoneLabel>> trainingElements;

        SVMZoneClassifier metaBodyRefClassifier = new SVMZoneClassifier(featureVectorBuilder);
        {
        	svm_parameter param = SVMZoneClassifier.getDefaultParam();
			param.svm_type = svm_parameter.C_SVC;
			param.gamma = 0.176776695297;
			param.C = 4.0;
			param.degree = 2;
			param.kernel_type = svm_parameter.POLY;
			metaBodyRefClassifier.setParameter(param);
			
			List<BxDocument> copiedTrainingDocuments = BxModelUtils.deepClone(trainingDocuments);
			for(BxDocument doc: copiedTrainingDocuments) {
				for(BxZone zone: doc.asZones()) {
					if(zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_OTHER)
						zone.setLabel(BxZoneLabel.GEN_BODY);
					else
						zone.setLabel(zone.getLabel().getGeneralLabel());
				}
			}
			trainingElements = node.process(copiedTrainingDocuments);
			trainingElements = selector.pickElements(trainingElements);
			
			System.out.println("building metaBodyRef");
			metaBodyRefClassifier.buildClassifier(trainingElements);
        }
        
		
        SVMZoneClassifier metaClassifier = new SVMZoneClassifier(featureVectorBuilder);
		{
			svm_parameter param = SVMZoneClassifier.getDefaultParam();
			param.svm_type = svm_parameter.C_SVC;
			param.gamma = 0.176776695297;
			param.C = 4.0;
			param.degree = 2;
			param.kernel_type = svm_parameter.POLY;
			metaClassifier.setParameter(param);
			
			List<BxDocument> copiedTrainingDocuments = BxModelUtils.deepClone(trainingDocuments);
			System.out.println("trainingDocuments " + trainingDocuments.size());
			for(BxDocument doc: copiedTrainingDocuments) {
				for(BxZone zone: doc.asZones()) {
					if(zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_OTHER)
						zone.setLabel(BxZoneLabel.GEN_BODY);
					if(zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_REFERENCES)
						zone.setLabel(BxZoneLabel.GEN_REFERENCES);
				}
			}
			
			trainingElements = node.process(copiedTrainingDocuments);
			System.out.println("trainingElements" + trainingElements.size());
			List<TrainingElement<BxZoneLabel>> toBeRemoved = new ArrayList<TrainingElement<BxZoneLabel>>();
			for(TrainingElement<BxZoneLabel> elem: trainingElements) {
				if(elem.getLabel().getGeneralLabel() != BxZoneLabel.GEN_METADATA)
					toBeRemoved.add(elem);
			}
			trainingElements.removeAll(toBeRemoved);
			System.out.println("trainingElements" + trainingElements.size());
			trainingElements = selector.pickElements(trainingElements);
			System.out.println("trainingElements" + trainingElements.size());
			System.out.println("building meta");
			metaClassifier.buildClassifier(trainingElements);
		}
		
        SVMZoneClassifier bodyOtherClassifier = new SVMZoneClassifier(featureVectorBuilder);
        {
        	svm_parameter param = SVMZoneClassifier.getDefaultParam();
			param.svm_type = svm_parameter.C_SVC;
			param.gamma = 0.176776695297;
			param.C = 4.0;
			param.degree = 2;
			param.kernel_type = svm_parameter.POLY;
			bodyOtherClassifier.setParameter(param);
			
			List<BxDocument> copiedTrainingDocuments = BxModelUtils.deepClone(trainingDocuments);
			for(BxDocument doc: copiedTrainingDocuments) {
				for(BxZone zone: doc.asZones()) {
					if(zone.getLabel().getGeneralLabel() == BxZoneLabel.GEN_BODY
						|| zone.getLabel().getGeneralLabel() == BxZoneLabel.GEN_OTHER
						|| zone.getLabel().getGeneralLabel() == BxZoneLabel.GEN_REFERENCES	)
						zone.setLabel(zone.getLabel().getGeneralLabel());
					else
						zone.setLabel(zone.getLabel().getGeneralLabel());
				}
			}
			
			trainingElements = node.process(copiedTrainingDocuments);
			System.out.println("trainingElements" + trainingElements.size());
			List<TrainingElement<BxZoneLabel>> toBeRemoved = new ArrayList<TrainingElement<BxZoneLabel>>();
			for(TrainingElement<BxZoneLabel> elem: trainingElements) {
				if(elem.getLabel().getGeneralLabel() != BxZoneLabel.GEN_BODY
					&& elem.getLabel().getGeneralLabel() != BxZoneLabel.GEN_OTHER)
					toBeRemoved.add(elem);
			}
			trainingElements.removeAll(toBeRemoved);
			System.out.println("trainingElements" + trainingElements.size());
			trainingElements = selector.pickElements(trainingElements);
			System.out.println("trainingElements" + trainingElements.size());
			System.out.println("building bodyOtherClassifier");
			bodyOtherClassifier.buildClassifier(trainingElements);
        }
        PipelineClassifier classifier = new PipelineClassifier();
        classifier.addClassifier(new PickyClassifier(metaBodyRefClassifier) {
        	public Boolean shouldBeClassified(BxZone zone) {
        		return true;
        	}
        });
        classifier.addClassifier(new PickyClassifier(metaClassifier) {
        	public Boolean shouldBeClassified(BxZone zone) {
        		if(zone.getLabel() == BxZoneLabel.GEN_METADATA)
        			return true;
        		else
        			return false;
        	}
        });
        classifier.addClassifier(new PickyClassifier(bodyOtherClassifier) {
        	public Boolean shouldBeClassified(BxZone zone) {
        		if(zone.getLabel() == BxZoneLabel.GEN_BODY)
        			return true;
        		else
        			return false;
        	}
        });
        return classifier;
	}

	public static void main(String[] args) throws ParseException, RuntimeException, AnalysisException {
		CrossvalidatingZoneClassificationEvaluator.main(args, new SVMZoneClassificationEvaluator());
	}
}
