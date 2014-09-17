package pl.edu.icm.cermine.metadata.affiliations.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliations.features.AffiliationDictionaryFeature;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.features.*;
import pl.edu.icm.cermine.parsing.tools.FeatureExtractor;

/**
 * Feature extractor suitable for processing affiliations.
 * 
 * @author Bartosz Tarnawski
 */
public class AffiliationFeatureExtractor extends FeatureExtractor<DocumentAffiliation> {

	private List<BinaryTokenFeatureCalculator> binaryFeatures;
	private List<KeywordFeatureCalculator<AffiliationToken>> keywordFeatures;
	private WordFeatureCalculator wordFeature;
	
	@SuppressWarnings("unchecked")
	public AffiliationFeatureExtractor() throws AnalysisException {
		binaryFeatures = 
			new ArrayList<BinaryTokenFeatureCalculator>(
					Arrays.<BinaryTokenFeatureCalculator>asList(
                        new IsNumberFeature(),
                        new IsUpperCaseFeature(),
                        new IsAllUpperCaseFeature(),
                        new IsAllLowerCaseFeature()
                        // new IsWordFeature(),
                        // new IsSeparatorFeature(),
                        // new IsNonAlphanumFeature(),
                        ));
		
		keywordFeatures = Arrays.<KeywordFeatureCalculator<AffiliationToken>>asList(
			new AffiliationDictionaryFeature("KeywordAddress", 		"address_keywords.txt", 	false),
			new AffiliationDictionaryFeature("KeywordCountry", 		"countries2.txt", 			true),
			new AffiliationDictionaryFeature("KeywordInstitution", 	"institution_keywords.txt", false)
			// new AffiliationDictionaryFeature("KeywordCity", 			"cities.txt", 				true),
			// new AffiliationDictionaryFeature("KeywordState", 		"states.txt", 				true),
			// new AffiliationDictionaryFeature("KeywordStateCode", 	"state_codes.txt", 			true),
			// new AffiliationDictionaryFeature("KeywordStopWord",		"stop_words_multilang.txt", false),
			);
		
		wordFeature = 
			new WordFeatureCalculator(Arrays.<BinaryTokenFeatureCalculator>asList(
					new IsNumberFeature()), false);
	}
	
	
	/**
	 * @param commonWords the words that are not considered 'Rare'
	 * @throws AnalysisException 
	 */
	public AffiliationFeatureExtractor(List<String> commonWords) throws AnalysisException {
		this();
		binaryFeatures.add(new IsRareFeature(commonWords, true));
	}
	
	public AffiliationFeatureExtractor(List<BinaryTokenFeatureCalculator> binaryFeatures,
			List<KeywordFeatureCalculator<AffiliationToken>> keywordFeatures,
			WordFeatureCalculator wordFeature) {
		this.binaryFeatures = binaryFeatures;
		this.keywordFeatures = keywordFeatures;
		this.wordFeature = wordFeature;
	}
	

	@Override
	public void calculateFeatures(DocumentAffiliation affiliation) {
		
		List<AffiliationToken> tokens = affiliation.getTokens();
		for (AffiliationToken token : tokens) {
			for (BinaryTokenFeatureCalculator binaryFeatureCalculator : binaryFeatures) {
				if (binaryFeatureCalculator.calculateFeaturePredicate(token, affiliation)) {
					token.addFeature(binaryFeatureCalculator.getFeatureName());
				}
			}
			String wordFeatureString = wordFeature.calculateFeatureValue(token, affiliation);
			if (wordFeatureString != null) {
				token.addFeature(wordFeatureString);
			}
		}
		
		for (KeywordFeatureCalculator<AffiliationToken> dictionaryFeatureCalculator :
			keywordFeatures) {
			dictionaryFeatureCalculator.calculateDictionaryFeatures(tokens);
		}
	}
}
