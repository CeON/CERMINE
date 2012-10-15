package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/** 
 * @author Pawel Szostek (p.szostek@icm.edu.pl) 
 */

public class WhitespaceCountFeature extends FeatureCalculator<BxZone, BxPage> {

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		int spaceCount = 0;
		for(Character c: zone.toText().toCharArray()) {
			if (Character.isWhitespace(c)) {
				++spaceCount;
			}
		}
		return spaceCount;
	}
}
