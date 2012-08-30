package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.util.List;

import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMServiceImpl;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfoFactory;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes.BxDocsToFVHMMTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.textr.HMMZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabelCategory;

/*
 *  @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public class HMMZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator
{
	@Override
	protected HMMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder)
	{
		System.out.println("HMM");
        BxDocsToFVHMMTrainingElementsConverterNode node = new BxDocsToFVHMMTrainingElementsConverterNode();
        node.setFeatureVectorBuilder(featureVectorBuilder);
        node.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
        
        List<HMMTrainingElement<BxZoneLabel>> trainingElements;
        try {
        	trainingElements = node.process(trainingDocuments, null);
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
}
