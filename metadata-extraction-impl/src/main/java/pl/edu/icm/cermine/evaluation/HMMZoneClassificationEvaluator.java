package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.ParseException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.cermine.structure.HMMZoneClassifier;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.HMMServiceImpl;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfoFactory;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

/*
 *  @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public class HMMZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator
{
	@Override
	protected HMMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments)
	{
		System.out.println("HMM");
		FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();
        BxDocsToHMMConverter node = new BxDocsToHMMConverter();
        node.setFeatureVectorBuilder(featureVectorBuilder);
        node.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
        
        List<TrainingElement<BxZoneLabel>> trainingElements;
        try {
        	trainingElements = node.process(trainingDocuments);
        } catch(Exception e) {
			throw new RuntimeException("Unable to process the delivered training documents!");
		}
        
		HMMProbabilityInfo<BxZoneLabel> hmmProbabilities;
		try {
			hmmProbabilities = HMMProbabilityInfoFactory.getFVHMMProbability(trainingElements, featureVectorBuilder);
		} catch(Exception e) {
			throw new RuntimeException("Unable to figure out HMM probability information!");
		}
		
		HMMZoneClassifier zoneClassifier = new HMMZoneClassifier(
				new HMMServiceImpl(),
				hmmProbabilities,
				BxZoneLabel.valuesOfCategory(BxZoneLabelCategory.CAT_GENERAL),
				featureVectorBuilder);
		return zoneClassifier;
	}
	
	public static void main(String[] args) throws ParseException, RuntimeException, AnalysisException, IOException {
		CrossvalidatingZoneClassificationEvaluator.main(args, new HMMZoneClassificationEvaluator());
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
