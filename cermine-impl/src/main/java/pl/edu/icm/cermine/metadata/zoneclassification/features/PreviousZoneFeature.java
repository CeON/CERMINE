package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public class PreviousZoneFeature extends FeatureCalculator<BxZone, BxPage>
{
	private static String featureName = "PreviousZoneFeature";
	
	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		if(object.hasPrev()) {
			if(object.getPrev().getLabel() != null) {
				return (double) object.getPrev().getLabel().ordinal();
			} else {
				return -2.0;
			}
		} else {
			return -1.0;
		}
	}
}
