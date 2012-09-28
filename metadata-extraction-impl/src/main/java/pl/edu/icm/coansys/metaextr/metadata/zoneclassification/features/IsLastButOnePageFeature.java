package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

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
