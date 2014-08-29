package pl.edu.icm.cermine.affparse.tools;

import java.util.Arrays;
import java.util.List;

import pl.edu.icm.cermine.affparse.features.*;
import pl.edu.icm.cermine.affparse.model.AffiliationToken;

public class AffiliationFeatureExtractor {

	public static void extractFeatures(List<AffiliationToken> tokens) {
		for (Feature feature : localFeatures) {
			for (AffiliationToken token : tokens) {
				String computedFeature = feature.computeFeature(token.getText());
				if (computedFeature != null) {
					token.addFeature(computedFeature);
				}
			}
		}
	}
	
	private static final List<Feature> localFeatures = Arrays.asList(
			(Feature)new IsWord(),
			(Feature)new IsNumber(),
			(Feature)new IsUpperCase(),
			(Feature)new WordFeature(Arrays.asList((Feature)new IsNumber()))
			);
}
