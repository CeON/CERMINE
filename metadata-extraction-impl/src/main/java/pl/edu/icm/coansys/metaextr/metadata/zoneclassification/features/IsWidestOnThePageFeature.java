package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

public class IsWidestOnThePageFeature implements FeatureCalculator<BxZone, BxPage> {
	private String featureName = "IsWidestOnThePage" ;
	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		for(BxZone zone: context.getZones())
			if(zone.getWidth() > object.getWidth())
				return 0.0;
		return 1.0;
	}

}
