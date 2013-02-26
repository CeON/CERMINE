package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public class LastButOneZoneFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		if(object.hasPrev()) {
			if(object.getPrev().hasPrev()) {
				return (double) object.getPrev().getPrev().getLabel().ordinal();
			}
		} 
		return -1.0;
	}
}
