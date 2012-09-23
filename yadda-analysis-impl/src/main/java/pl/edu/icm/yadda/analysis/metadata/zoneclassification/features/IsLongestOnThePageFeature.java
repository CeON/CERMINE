package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

public class IsLongestOnThePageFeature implements FeatureCalculator<BxZone, BxPage> {
	private String featureName = "IsLongestOnThePage";
	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		for(BxZone zone: context.getZones())
			if(zone.toText().length() > object.toText().length())
				return 0.0;
		return 1.0;
	}

}
