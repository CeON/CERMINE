package pl.edu.icm.cermine.parsing.tools;

import java.util.List;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.parsing.model.Token;

/**
 * Class for predicting token labels.
 * 
 * @author Bartosz Tarnawski
 * @param <T> token type
 */
public abstract class TokenClassifier<T extends Token<?>> {
	/**
	 * Predicts and sets a label for each of the tokens.
	 * The tokens are assumed to hold appropriate lists of features.
	 * 
	 * @param tokens
	 * @throws AnalysisException
	 */
	public abstract void classify(List<T> tokens) throws AnalysisException; 
}
