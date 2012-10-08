package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public class EmptySpaceRelativeFeature extends FeatureCalculator<BxZone, BxPage> {
	
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
