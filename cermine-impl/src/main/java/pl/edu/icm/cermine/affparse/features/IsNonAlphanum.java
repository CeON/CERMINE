package pl.edu.icm.cermine.affparse.features;

import pl.edu.icm.cermine.affparse.tools.TextClassifier;

public class IsNonAlphanum extends BinaryFeature {

	@Override
	public String computeFeature(String text) {
		if (TextClassifier.isNonAlphanumSep(text)) {
			assert text.length() == 1;
			return featureString;
		}
		return null;
	}
	
	private static final String featureString = "WeirdLetter"; // TODO change to "NonAlphanum"

}
