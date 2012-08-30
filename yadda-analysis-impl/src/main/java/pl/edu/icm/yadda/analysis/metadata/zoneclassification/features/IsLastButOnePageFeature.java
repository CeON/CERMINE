package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

public class IsLastButOnePageFeature implements FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "IsLastButOnePage";

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		if(page.getNext() != null)
			if(page.getNext().getNext() == null)
				return 1.0;
		return 0.0;
	}
}
