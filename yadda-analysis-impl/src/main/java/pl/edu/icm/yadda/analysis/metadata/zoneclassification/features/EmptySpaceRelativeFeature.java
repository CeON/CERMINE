package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import java.util.List;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.EmptySpaceFeature.AreaCalculator;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.EmptySpaceFeature.ConvexHullCalculator;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.EmptySpaceFeature.Point;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

public class EmptySpaceRelativeFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "EmptySpaceRelative";

	@Override
	public String getFeatureName() {
		return featureName;
	}
	
	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {	
		FeatureCalculator<BxZone, BxPage> emptySpaceCalc = new EmptySpaceFeature(); 
		ConvexHullCalculator hullCalculator = new ConvexHullCalculator();
		List<Point> hull = hullCalculator.calculateConvexHull(zone);
		AreaCalculator areaCalculator = new AreaCalculator();
		double zoneArea = areaCalculator.calculateArea(hull);
		double emptySpace = emptySpaceCalc.calculateFeatureValue(zone, page);
		return emptySpace/zoneArea;
	}
}
