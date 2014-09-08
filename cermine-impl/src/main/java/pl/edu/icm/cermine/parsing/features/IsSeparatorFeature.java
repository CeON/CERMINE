package pl.edu.icm.cermine.parsing.features;

import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.model.TokenizedString;
import pl.edu.icm.cermine.parsing.tools.TextClassifier;

/**
 * @author Bartosz Tarnawski
 *
 */
public class IsSeparatorFeature extends BinaryTokenFeatureCalculator {

	@Override
	public boolean calculateFeaturePredicate(Token<?> token, TokenizedString<?> context) {
		return TextClassifier.isSeparator(token.getText());
	}

}
