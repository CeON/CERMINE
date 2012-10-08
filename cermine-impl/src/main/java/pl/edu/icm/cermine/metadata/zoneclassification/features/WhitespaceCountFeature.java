package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/** 
 * @author Pawel Szostek (p.szostek@icm.edu.pl) 
 */

public class WhitespaceCountFeature implements FeatureCalculator<BxZone, BxPage> {
	private static String featureName = "WhitespaceCount";

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		Integer spaceCount = 0;
		for(Character c: zone.toText().toCharArray())
			if (Character.isWhitespace(c)) {
				++spaceCount;
			}
		return new Double(spaceCount);
	}
}
