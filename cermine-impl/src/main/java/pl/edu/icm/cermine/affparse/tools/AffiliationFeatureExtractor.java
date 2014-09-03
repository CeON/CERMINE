package pl.edu.icm.cermine.affparse.tools;

import java.util.Arrays;
import java.util.List;

import pl.edu.icm.cermine.affparse.dictfeatures.*;
import pl.edu.icm.cermine.affparse.features.*;
import pl.edu.icm.cermine.affparse.model.AffiliationToken;

public class AffiliationFeatureExtractor {

	public static void extractFeatures(List<AffiliationToken> tokens) {
		for (LocalFeature feature : localFeatures) {
			for (AffiliationToken token : tokens) {
				String computedFeature = feature.computeFeature(token.getText());
				if (computedFeature != null) {
					token.addFeature(computedFeature);
				}
			}
		}
		for (DictionaryFeature feature : dictFeatures) {
			feature.addFeatures(tokens);
		}
	}
	
	private static final List<LocalFeature> localFeatures = Arrays.<LocalFeature>asList(
			// new IsWord(), TODO dodać ? Będzie prawie wszędzie...
			new IsNumber(),
			new IsUpperCase(),
			new IsAllUpperCase(),
			new IsSeparator(),
			new IsNonAlphanum(),
			new WordFeature(Arrays.asList((LocalFeature)new IsNumber()), false)
			);
	
	private static final List<DictionaryFeature> dictFeatures = Arrays.<DictionaryFeature>asList(
			new Address(true),
			new City(false),
			new Country(false),
			new State(false),
			new StateCode(false),
			new StopWord(true)
			);
}
