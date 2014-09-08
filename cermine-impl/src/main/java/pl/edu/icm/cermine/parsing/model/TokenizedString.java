package pl.edu.icm.cermine.parsing.model;

import java.util.List;

/**
 * Representation of a tokenized string, ie. a string which is split into smaller parts (e.g. words)
 * 
 * @author Bartosz Tarnawski
 * @param <T> type of the tokens
 */
public interface TokenizedString<T extends Token<?>> {
	public abstract List<T> getTokens();
	public abstract void setTokens(List<T> tokens);
	public abstract String getRawText();
	public abstract void addToken(T token);
	public abstract void appendText(String text);
}
