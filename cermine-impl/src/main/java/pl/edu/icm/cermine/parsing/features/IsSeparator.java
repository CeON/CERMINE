package pl.edu.icm.cermine.parsing.features;

import pl.edu.icm.cermine.parsing.tools.TextClassifier;

public class IsSeparator extends BinaryFeature {

	@Override
	public String computeFeature(String text) {
		if (TextClassifier.isSeparator(text)) {
			return featureString;
		}
		return null;
	}
	
	private static final String featureString = "Punct"; // TODO change to "Separator"

}
