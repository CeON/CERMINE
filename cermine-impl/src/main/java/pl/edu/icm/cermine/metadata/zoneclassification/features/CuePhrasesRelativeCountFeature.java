package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public class CuePhrasesRelativeCountFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "CuePhrasesRelativeCount";

	@Override
	public String getFeatureName() {
		return featureName;
	}
	
	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {	
		FeatureCalculator<BxZone, BxPage> cuePhrasesCalc = new ContainsCuePhrasesFeature();
		FeatureCalculator<BxZone, BxPage> wordsCalc = new WordCountFeature();
		double cuePhrasesCountValue = cuePhrasesCalc.calculateFeatureValue(zone, page);
		double wordCountValue = wordsCalc.calculateFeatureValue(zone, page);
		return cuePhrasesCountValue/wordCountValue;
	}
}
