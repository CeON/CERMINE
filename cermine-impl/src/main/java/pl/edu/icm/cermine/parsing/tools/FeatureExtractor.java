package pl.edu.icm.cermine.parsing.tools;

import pl.edu.icm.cermine.parsing.model.TokenizedString;

public abstract class FeatureExtractor<T extends TokenizedString<?>> {
	
	public abstract void calculateFeatures(T string);
}
