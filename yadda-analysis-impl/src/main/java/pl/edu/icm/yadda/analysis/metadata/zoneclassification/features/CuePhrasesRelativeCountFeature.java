package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

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
