package pl.edu.icm.cermine.affparse.model;

import java.util.List;
import org.jdom.Element;


public abstract class TokenizedString<L extends Label, T extends Token<L>> {
	
	protected String text;
	protected List<T> tokens;
	public List<T> getTokens() {
		return tokens;
	}
	
	public abstract Element toNLM();
	
}
