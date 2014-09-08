package pl.edu.icm.cermine.parsing.features;

import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.model.TokenizedString;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public abstract class BinaryTokenFeatureCalculator extends
FeatureCalculator<Token<?>, TokenizedString<?>> {

	public abstract boolean calculateFeaturePredicate(Token<?> token, TokenizedString<?> context);

	@Override
    public double calculateFeatureValue(Token<?> token, TokenizedString<?> context) {
    	if (calculateFeaturePredicate(token, context)) {
    		return 1;
    	} else {
    		return 0;
    	}
    }
}
