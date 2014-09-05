package pl.edu.icm.cermine.parsing.features;

import java.util.List;

import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public abstract class BinaryTokenFeatureCalculator extends FeatureCalculator<Token, List<? extends Token>> {

	public abstract boolean calculateFeaturePredicate(Token token, List<? extends Token> context);
	
    public double calculateFeatureValue(Token token, List<? extends Token> context) {
    	if (calculateFeaturePredicate(token, context)) {
    		return 1;
    	} else {
    		return 0;
    	}
    }
}
