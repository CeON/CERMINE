package pl.edu.icm.cermine.parsing.model;

import java.util.List;

public abstract class TokenizedString<T extends Token<?>> {
	public abstract List<T> getTokens();
	public abstract void setTokens(List<T> tokens);
}
