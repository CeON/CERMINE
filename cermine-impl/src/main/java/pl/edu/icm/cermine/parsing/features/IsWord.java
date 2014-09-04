package pl.edu.icm.cermine.parsing.features;

import pl.edu.icm.cermine.parsing.tools.TextClassifier;

public class IsWord extends BinaryFeature {

	@Override
	public String computeFeature(String text) {
		if (TextClassifier.isWord(text)) {
			return featureString;
		}
		return null;
	}
	
	private static final String featureString = "IsWord";
	
}
