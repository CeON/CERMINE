package pl.edu.icm.cermine.parsing.tools;

import java.util.List;

import pl.edu.icm.cermine.parsing.model.Token;

public abstract class FeatureExtractor<L, T extends Token<L>> {
	
	public abstract void calculateFeatures(List<T> tokens);
	
	/*{
		for (LocalFeature feature : getLocalFeatures()) {
			for (T token : tokens) {
				String computedFeature = feature.computeFeature(token.getText());
				if (computedFeature != null) {
					token.addFeature(computedFeature);
				}
			}
		}
		for (DictionaryFeature<L, T> feature : getDictionaryFeatures()) {
			feature.addFeatures(tokens);
		}
	}
		
	protected abstract List<LocalFeature> getLocalFeatures();
	protected abstract List<DictionaryFeature<L, T>> getDictionaryFeatures();*/
}
