package pl.edu.icm.cermine.parsing.features;

import pl.edu.icm.cermine.parsing.tools.TextClassifier;

public class IsUpperCase extends BinaryFeature {

	@Override
	public String computeFeature(String text) {
		if (TextClassifier.isOnlyFirstUpperCase(text)) {
			return featureString;
		}
		return null;
	}
	
	private static final String featureString = "UpperCase";

}
