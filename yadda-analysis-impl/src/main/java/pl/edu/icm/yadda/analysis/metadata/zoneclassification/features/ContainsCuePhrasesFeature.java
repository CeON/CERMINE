package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxChunk;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

public class ContainsCuePhrasesFeature implements
		FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "ContainsCuePhrases";
	private static String[] cuePhrases = {"although", "therefore", "therein", "hereby",
			"nevertheless", "to this end", "however", "moreover", "nonetheless" };

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		String zoneText = zone.toText().toLowerCase();
		int count = 0;
		
		for(String cuePhrase: cuePhrases) {
			if(!zoneText.contains(cuePhrase))
				continue;
			else
				return 1.0;
		}
		return 0.0;
	}
}
