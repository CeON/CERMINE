package pl.edu.icm.cermine.parsing.tools;

import pl.edu.icm.cermine.parsing.model.TokenizedString;

/**
 * Finds features of tokens in a tokenized string.
 * 
 * @author Bartosz Tarnawski
 * @param <T> type of the tokenized string to process
 */
public abstract class FeatureExtractor<T extends TokenizedString<?>> {
	
	/**
	 * Adds appropriate strings representing features to the tokens of the tokenized string.
	 * 
	 * @param string the tokenized string to be processed
	 */
	public abstract void calculateFeatures(T string);
}
