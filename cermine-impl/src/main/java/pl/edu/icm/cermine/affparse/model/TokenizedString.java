package pl.edu.icm.cermine.affparse.model;

import java.util.List;
import org.jdom.Element;

import pl.edu.icm.cermine.exception.AnalysisException;


public abstract class TokenizedString<L extends Label, T extends Token<L>> {
	
	protected String text;
	protected List<T> tokens;
	public List<T> getTokens() {
		return tokens;
	}
	
	public abstract void calculateFeatures();
	
	public abstract void classify() throws AnalysisException;
	
	public abstract Element toNLM();
	
}
