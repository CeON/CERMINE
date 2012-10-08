package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/** 
 * @author Pawel Szostek (p.szostek@icm.edu.pl) 
 */

public class FreeSpaceWithinZoneFeature implements FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "FreeSpace";

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		double charSpace = 0.0;

		for (BxLine line : zone.getLines()) {
			for (BxWord word : line.getWords())
				for (BxChunk chunk : word.getChunks()) {
					charSpace += chunk.getArea();
				}
		}
		return zone.getArea() - charSpace;
	}

}
