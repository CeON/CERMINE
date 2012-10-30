package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;

public class IsSingleWordFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		String text = object.toText();
		if(text.length() == 0) //zone is empty
			return 0.0;
		String[] parts = text.split(" \\n\\t");
		if(parts.length == 1) { //white characters were not found
			return 1.0;
		}
		Boolean foundNonEmpty = false;
		for(String part: parts) {
			if(part.length() == 0) { //empty string => ommit
				continue;
			} else {
				if(foundNonEmpty) { //already found a non-empty stting => there are many words
					return 0.0;
				} else { //mark that a non-empty string was found
					foundNonEmpty = true;
				}
			}
		}
		return 1.0;
	}

}
