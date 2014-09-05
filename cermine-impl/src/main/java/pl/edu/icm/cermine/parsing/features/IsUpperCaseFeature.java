package pl.edu.icm.cermine.parsing.features;

import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.model.TokenizedString;
import pl.edu.icm.cermine.parsing.tools.TextClassifier;

public class IsUpperCaseFeature extends BinaryTokenFeatureCalculator {

	@Override
	public boolean calculateFeaturePredicate(Token<?> token, TokenizedString<?> context) {
		return TextClassifier.isOnlyFirstUpperCase(token.getText()); 
	}

}
