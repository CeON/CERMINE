package pl.edu.icm.cermine.parsing.model;

import java.util.List;

public interface TokenizedString<T extends Token<?>> {
	public abstract List<T> getTokens();
	public abstract void setTokens(List<T> tokens);
	public abstract String getRawText();
	public abstract void addToken(T token);
	public abstract void appendText(String text);
}
