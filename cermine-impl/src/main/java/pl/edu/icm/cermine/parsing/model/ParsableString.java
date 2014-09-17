package pl.edu.icm.cermine.parsing.model;

import java.util.List;

/**
 * Representation of a parsable string.
 * Such a string may be tokenized i.e. split into smaller parts (for example words).
 * Each token may have a label corresponding to the type of its text content.
 * The parsing process consists of tokenization followed by token classification.
 * 
 * @author Bartosz Tarnawski
 * @param <T> type of the tokens
 */
public interface ParsableString<T extends Token<?>> {
	/**
	 * @return tokens corresponding to the text content
	 */
	List<T> getTokens();
	/**
	 * @param tokens the tokens corresponding to the text content of the parsable string
	 */
	void setTokens(List<T> tokens);
	/**
	 * @return the text content
	 */
	String getRawText();
	/**
	 * @param append token to the end of the token list
	 */
	void addToken(T token);
	/**
	 * Appends the text to the text content
	 * 
	 * @param text 
	 */
	void appendText(String text);
	/**
	 * Cleans the text content
	 */
	void clean();
}
