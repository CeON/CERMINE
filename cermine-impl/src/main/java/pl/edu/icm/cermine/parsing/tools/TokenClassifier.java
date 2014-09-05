package pl.edu.icm.cermine.parsing.tools;

import java.util.List;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.parsing.model.Token;

public abstract class TokenClassifier<T extends Token<?>> {
	public abstract void classify(List<T> tokens) throws AnalysisException; 
}
