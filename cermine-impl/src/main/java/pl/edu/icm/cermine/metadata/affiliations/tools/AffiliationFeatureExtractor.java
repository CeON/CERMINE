package pl.edu.icm.cermine.metadata.affiliations.tools;

import java.util.Arrays;
import java.util.List;

import pl.edu.icm.cermine.metadata.affiliations.features.AffiliationDictionaryFeature;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.parsing.features.*;
import pl.edu.icm.cermine.parsing.tools.FeatureExtractor;

public class AffiliationFeatureExtractor extends FeatureExtractor<AffiliationLabel, AffiliationToken> {
	
	private static final List<BinaryTokenFeatureCalculator> binaryFeatures = 
			Arrays.<BinaryTokenFeatureCalculator>asList(
			// new IsWord(), TODO We could add this but it would appear almost everywhere...
			new IsNumberFeature(),
			new IsUpperCaseFeature(),
			new IsAllUpperCaseFeature(),
			new IsSeparatorFeature(),
			new IsNonAlphanumFeature()
			);
	
	@SuppressWarnings("unchecked")
	private static final List<DictionaryFeature<AffiliationLabel, AffiliationToken>>
	dictionaryFeatures = Arrays.<DictionaryFeature<AffiliationLabel, AffiliationToken>>asList(
			new AffiliationDictionaryFeature("KeywordAddress", 		"address_keywords.txt", 	true),
			new AffiliationDictionaryFeature("KeywordCity", 		"cities.txt", 				false),
			new AffiliationDictionaryFeature("KeywordCountry", 		"countries2.txt", 			false),
			new AffiliationDictionaryFeature("KeywordState", 		"states.txt", 				false),
			new AffiliationDictionaryFeature("KeywordStateCode", 	"state_codes.txt", 			false),
			new AffiliationDictionaryFeature("KeywordStopWord",		"stop_words_multilang.txt", true)
			);
	
	private static final WordFeatureCalculator wordFeatureCalculator = 
			new WordFeatureCalculator(Arrays.<BinaryTokenFeatureCalculator>asList(
					new IsNumberFeature()), false);

	@Override
	public void calculateFeatures(List<AffiliationToken> tokens) {
		
		// TODO this can be done also with the use of FeatureVectorBuilder
		for (AffiliationToken token : tokens) {
			for (BinaryTokenFeatureCalculator binaryFeatureCalculator : binaryFeatures) {
				if (binaryFeatureCalculator.calculateFeaturePredicate(token, tokens)) {
					token.addFeature(binaryFeatureCalculator.getFeatureName());
				}
			}
			String wordFeatureString = wordFeatureCalculator.calculateFeatureValue(token, tokens);
			if (wordFeatureString != null) {
				token.addFeature(wordFeatureString);
			}
		}
		
		for (DictionaryFeature<AffiliationLabel, AffiliationToken> dictionaryFeatureCalculator :
			dictionaryFeatures) {
			dictionaryFeatureCalculator.calculateDictionaryFeatures(tokens);
		}
	}
}
