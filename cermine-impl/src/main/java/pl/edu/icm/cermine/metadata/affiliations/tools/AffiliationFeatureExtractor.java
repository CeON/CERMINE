package pl.edu.icm.cermine.metadata.affiliations.tools;

import java.util.Arrays;
import java.util.List;

import pl.edu.icm.cermine.metadata.affiliations.features.AffiliationDictionaryFeature;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.features.*;
import pl.edu.icm.cermine.parsing.tools.FeatureExtractor;

public class AffiliationFeatureExtractor extends FeatureExtractor<DocumentAffiliation> {
	
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
	private static final List<DictionaryFeatureCalculator<AffiliationToken>>
	dictionaryFeatureCalculators = Arrays.<DictionaryFeatureCalculator<AffiliationToken>>asList(
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
	public void calculateFeatures(DocumentAffiliation affiliation) {
		
		List<AffiliationToken> tokens = affiliation.getTokens();
		for (AffiliationToken token : tokens) {
			for (BinaryTokenFeatureCalculator binaryFeatureCalculator : binaryFeatures) {
				if (binaryFeatureCalculator.calculateFeaturePredicate(token, affiliation)) {
					token.addFeature(binaryFeatureCalculator.getFeatureName());
				}
			}
			String wordFeatureString = wordFeatureCalculator.calculateFeatureValue(token,
					affiliation);
			if (wordFeatureString != null) {
				token.addFeature(wordFeatureString);
			}
		}
		
		for (DictionaryFeatureCalculator<AffiliationToken> dictionaryFeatureCalculator :
			dictionaryFeatureCalculators) {
			dictionaryFeatureCalculator.calculateDictionaryFeatures(tokens);
		}
		
		affiliation.setTokens(tokens);
	}
}
