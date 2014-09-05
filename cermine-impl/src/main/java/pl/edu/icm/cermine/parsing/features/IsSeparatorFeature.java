package pl.edu.icm.cermine.parsing.features;

import java.util.List;

import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.tools.TextClassifier;

public class IsSeparatorFeature extends BinaryTokenFeatureCalculator {

	@Override
	public boolean calculateFeaturePredicate(Token token, List<? extends Token> context) {
		return TextClassifier.isSeparator(token.getText());
	}

}
