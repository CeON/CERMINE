package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public class EmptySpaceFeature implements FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "EmptySpace";
	
	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		double charSpace = 0.0;
		for (BxLine line : zone.getLines()) {
			for (BxWord word : line.getWords()) {
				for (BxChunk chunk : word.getChunks()) {
					charSpace += chunk.getArea();
				}
			}
		}
		double ret = zone.getArea() - charSpace;
		if(ret < 0) {
			return 0.0;
		} else {
			return ret;
		}
	}
}
