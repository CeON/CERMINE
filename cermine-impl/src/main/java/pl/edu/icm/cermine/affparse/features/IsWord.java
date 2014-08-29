package pl.edu.icm.cermine.affparse.features;

import pl.edu.icm.cermine.affparse.tools.TextClassifier;

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
