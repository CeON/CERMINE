package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.features.AbstractFeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;

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
