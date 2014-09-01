package pl.edu.icm.cermine.affparse.features;

import pl.edu.icm.cermine.affparse.tools.TextClassifier;

public class IsNumber extends BinaryFeature {

	@Override
	public String computeFeature(String text) {
		if (TextClassifier.isNumber(text)) {
			return featureString;
		}
		return null;
	}
	
	private static final String featureString = "Number";

}
