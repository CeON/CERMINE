package pl.edu.icm.cermine.parsing.tools;

import pl.edu.icm.cermine.parsing.model.ParsableString;

/**
 * Finds features of tokens in a tokenized parsable string.
 * 
 * @author Bartosz Tarnawski
 * @param <PS> type of the parsable string to process
 */
public interface FeatureExtractor<PS extends ParsableString<?>> {
	
	/**
	 * Adds appropriate strings representing features to the tokens of the parsable string.
	 * 
	 * @param parsableString the tokenized parsable string to be processed
	 */
	void calculateFeatures(PS parsableString);
}
