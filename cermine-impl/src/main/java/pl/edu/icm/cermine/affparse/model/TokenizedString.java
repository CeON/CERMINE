package pl.edu.icm.cermine.affparse.model;

import java.util.List;


public abstract class TokenizedString<L extends Label, T extends Token<L>> {
	
	protected String text;
	protected List<T> tokens;
	public List<T> getTokens() {
		return tokens;
	}
	
}
