package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

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
