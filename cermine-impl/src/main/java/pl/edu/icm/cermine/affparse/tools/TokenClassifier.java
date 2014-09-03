package pl.edu.icm.cermine.affparse.tools;

import java.util.List;

import pl.edu.icm.cermine.affparse.model.Token;
import pl.edu.icm.cermine.exception.AnalysisException;

public abstract class TokenClassifier<L, T extends Token<L>> {
	public abstract void classify(List<T> tokens) throws AnalysisException; 
}
