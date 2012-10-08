package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

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
