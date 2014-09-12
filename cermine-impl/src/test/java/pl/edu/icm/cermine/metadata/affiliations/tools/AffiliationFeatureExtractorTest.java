package pl.edu.icm.cermine.metadata.affiliations.tools;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliations.features.AffiliationDictionaryFeature;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationFeatureExtractor;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.features.BinaryTokenFeatureCalculator;
import pl.edu.icm.cermine.parsing.features.IsAllUpperCaseFeature;
import pl.edu.icm.cermine.parsing.features.IsNonAlphanumFeature;
import pl.edu.icm.cermine.parsing.features.IsNumberFeature;
import pl.edu.icm.cermine.parsing.features.IsSeparatorFeature;
import pl.edu.icm.cermine.parsing.features.IsUpperCaseFeature;
import pl.edu.icm.cermine.parsing.features.KeywordFeatureCalculator;
import pl.edu.icm.cermine.parsing.features.WordFeatureCalculator;

public class AffiliationFeatureExtractorTest {

	private static final AffiliationTokenizer tokenizer = new AffiliationTokenizer();
	private static final AffiliationFeatureExtractor extractor;

	static {
		try {
			// Test all available features but IsRare, IsWord and IsAllLowerCase
			List<BinaryTokenFeatureCalculator> binaryFeatures = 
					Arrays.<BinaryTokenFeatureCalculator>asList(
                new IsNumberFeature(),
                new IsUpperCaseFeature(),
                new IsAllUpperCaseFeature(),
                // new IsAllLowerCaseFeature(), appears too often, too much typing
                new IsSeparatorFeature(),
                new IsNonAlphanumFeature()
                );
			
			@SuppressWarnings("unchecked")
			List<KeywordFeatureCalculator<AffiliationToken>> keywordFeatures = 
					Arrays.<KeywordFeatureCalculator<AffiliationToken>>asList(
                new AffiliationDictionaryFeature("KeywordAddress", 		"address_keywords.txt", 	false),
                new AffiliationDictionaryFeature("KeywordCity", 		"cities.txt", 				true),
                new AffiliationDictionaryFeature("KeywordCountry", 		"countries2.txt", 			true),
                new AffiliationDictionaryFeature("KeywordInstitution", 	"institution_keywords.txt", false),
                new AffiliationDictionaryFeature("KeywordState", 		"states.txt", 				true),
                new AffiliationDictionaryFeature("KeywordStateCode", 	"state_codes.txt", 			true),
                new AffiliationDictionaryFeature("KeywordStopWord",		"stop_words_multilang.txt", false)
                );
			
			WordFeatureCalculator wordFeature = 
					new WordFeatureCalculator(Arrays.<BinaryTokenFeatureCalculator>asList(
							new IsNumberFeature()), false);
			
			extractor = new AffiliationFeatureExtractor(binaryFeatures, keywordFeatures, wordFeature);
		} catch (AnalysisException e) {
			throw new RuntimeException("Failed to initialize the feature extractor");
		}
	}
	
	private class TokenContainer {
		public List<AffiliationToken> tokens;
		public List<List<String>> features;
		public TokenContainer() {
			tokens = new ArrayList<AffiliationToken>();
			features = new ArrayList<List<String>>();
		}
		public void add(String text, String... expectedFeatures) {
			tokens.add(new AffiliationToken(text));
			features.add(Arrays.asList(expectedFeatures));
		}
		public void checkFeatures() {
			for (int i = 0; i < tokens.size(); i++) {
				List<String> expected = features.get(i);
				List<String> actual = tokens.get(i).getFeatures();
				Collections.sort(expected);
				Collections.sort(actual);
				assertEquals(expected, actual);
				
			}
		}
	}
	
	@Test
	public void testExtractFeatures() {
		TokenContainer tc = new TokenContainer();
		
		tc.add("word", "W=word");
		tc.add("123", "IsNumber");
		tc.add("Uppercaseword", "W=Uppercaseword", "IsUpperCase");
		tc.add("ALLUPPERCASEWORD", "W=ALLUPPERCASEWORD", "IsAllUpperCase");
		tc.add(",", "W=,", "IsSeparator");
		tc.add("@", "W=@", "IsNonAlphanum");
		
		tc.add("Maluwang", "W=Maluwang", "IsUpperCase"); // sole "Maluwang" is not an address keyword
		
		tc.add("Maluwang", "W=Maluwang", "IsUpperCase", "KeywordAddress");
		tc.add("na", "W=na", "KeywordAddress");
		tc.add("lansangan", "W=lansangan", "KeywordAddress"); // maluwang na lansangan -- address keyword
		
		tc.add(".", "W=.", "IsSeparator");
		
		tc.add("les", "W=les");
		tc.add("escaldes","W=escaldes"); // Les Escaldes -- city keyword, needs uppercase

		tc.add("les", "W=les", "KeywordCity");
		tc.add("Escaldes","W=Escaldes", "KeywordCity", "IsUpperCase"); // Les Escaldes -- city keyword, needs uppercase
		
		tc.add("mhm", "W=mhm");
		
		tc.add("U", "W=U", "KeywordCountry", "IsUpperCase", "IsAllUpperCase");
		tc.add(".", "W=.", "IsSeparator", "KeywordCountry");
		tc.add("S", "W=S", "KeywordCountry", "IsUpperCase", "IsAllUpperCase");
		tc.add(".", "W=.", "IsSeparator", "KeywordCountry");
		tc.add("A", "W=A", "KeywordCountry", "IsUpperCase", "IsAllUpperCase");
		tc.add(".", "W=.", "IsSeparator", "KeywordCountry"); // U.S.A -- country keyword
		
		tc.add("New", "W=New", "IsUpperCase", "KeywordState");
		tc.add("Hampshire", "W=Hampshire", "IsUpperCase", "KeywordState"); // New Hampshire -- state keyword
		
		tc.add("KS", "W=KS", "IsAllUpperCase", "KeywordStateCode"); // KS -- state code keyword
		
		tc.add("du", "W=du", "KeywordStopWord"); // du -- a stop word
	
		
	    DocumentAffiliation instance = new DocumentAffiliation("someId", "");
	    instance.setTokens(tc.tokens);
		extractor.calculateFeatures(instance);
		tc.checkFeatures();
	}

	
	@Test
	public void testExtractFeaturesWithDocumentAffiliation() {
		String text = "Cóż ro123bić?";
	    List<List<String>> expectedFeatures = new ArrayList<List<String>>();
	    expectedFeatures.add(Arrays.asList("W=Coz", "IsUpperCase"));
	    expectedFeatures.add(Arrays.asList("W=ro"));
	    expectedFeatures.add(Arrays.asList("IsNumber"));
	    expectedFeatures.add(Arrays.asList("W=bic"));
	    expectedFeatures.add(Arrays.asList("W=?", "IsNonAlphanum"));
		
	    DocumentAffiliation instance = new DocumentAffiliation("someId", text);
	    instance.setTokens(tokenizer.tokenize(instance.getRawText()));
		extractor.calculateFeatures(instance);
		for (int i = 0; i < expectedFeatures.size(); i++) {
			List<String> expected = expectedFeatures.get(i);
			List<String> actual = instance.getTokens().get(i).getFeatures();
			Collections.sort(expected);
			Collections.sort(actual);
			assertEquals(expected, actual);
		}
	}
}
