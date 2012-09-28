package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.bibref.parsing.features.AbstractFeatureCalculator;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;

public class IsGreatestFontOnPageFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		FeatureCalculator<BxZone, BxPage> fc = new FontHeightMeanFeature();
		for(BxZone otherZone: getOtherZones(object)) {
			if(fc.calculateFeatureValue(otherZone, context) > fc.calculateFeatureValue(object, context))
				return 0.0;
		}
		return 1.0;
	}

}
