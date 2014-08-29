package pl.edu.icm.cermine.affparse.features;

import pl.edu.icm.cermine.affparse.tools.TextClassifier;

public class IsAllUpperCase extends BinaryFeature {

	@Override
	public String computeFeature(String text) {
		if (TextClassifier.isAllUpperCase(text)) {
			return featureString;
		}
		return null;
	}
	
	private static final String featureString = "AllUpperCase";

}
