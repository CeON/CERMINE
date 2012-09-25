package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

public class EmptySpaceRelativeFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "EmptySpaceRelative";

	@Override
	public String getFeatureName() {
		return featureName;
	}
	
	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {	
		FeatureCalculator<BxZone, BxPage> emptySpaceCalc = new EmptySpaceFeature(); 
		if(zone.getArea() < 0.0005) {
			return 0.0;
		} else {
			double emptySpace = emptySpaceCalc.calculateFeatureValue(zone, page);
			return emptySpace/zone.getArea();
		}
	}
}
