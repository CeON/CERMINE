package pl.edu.icm.cermine.parsing.features;

import java.util.List;

import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.model.TokenizedString;

public class WordFeatureCalculator {

	private List<BinaryTokenFeatureCalculator> blockingFeatures;
	private boolean toLowerCase;
	// This is a GRMM convention, see: https://dl.dropboxusercontent.com/u/55174954/grmm.htm
	private static final String PREFIX = "W=";
	
	public WordFeatureCalculator(List<BinaryTokenFeatureCalculator> blockingFeatures,
			boolean toLowerCase) {
		this.blockingFeatures = blockingFeatures;
		this.toLowerCase = toLowerCase;
	}
	
    public String calculateFeatureValue(Token<?> token, TokenizedString<?> context) {
		for (BinaryTokenFeatureCalculator feature : blockingFeatures) {
			if (feature.calculateFeaturePredicate(token, context)) {
				return null;
			}
		}
		if (toLowerCase) {
			return PREFIX + token.getText().toLowerCase(); 
		} else {
			return PREFIX + token.getText();
		}
    }
}
