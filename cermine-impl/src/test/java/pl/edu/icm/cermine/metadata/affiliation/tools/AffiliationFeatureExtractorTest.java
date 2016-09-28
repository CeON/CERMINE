/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */
package pl.edu.icm.cermine.metadata.affiliation.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliation.features.AffiliationDictionaryFeature;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.features.*;
import pl.edu.icm.cermine.parsing.model.Token;
import static org.junit.Assert.assertEquals;

/**
 * @author Bartosz Tarnawski
 */
public class AffiliationFeatureExtractorTest {

    private static final AffiliationTokenizer TOKENIZER = new AffiliationTokenizer();
    private static final AffiliationFeatureExtractor EXTRACTOR;

    static {
        try {
            List<BinaryTokenFeatureCalculator> binaryFeatures
                    = Arrays.<BinaryTokenFeatureCalculator>asList(
                            new IsNumberFeature(),
                            new IsUpperCaseFeature(),
                            new IsAllUpperCaseFeature(),
                            new IsSeparatorFeature(),
                            new IsNonAlphanumFeature()
                    );

            @SuppressWarnings("unchecked")
            List<KeywordFeatureCalculator<Token<AffiliationLabel>>> keywordFeatures
                    = Arrays.<KeywordFeatureCalculator<Token<AffiliationLabel>>>asList(
                            new AffiliationDictionaryFeature("KeywordAddress", "address_keywords.txt", false),
                            new AffiliationDictionaryFeature("KeywordCity", "cities.txt", true),
                            new AffiliationDictionaryFeature("KeywordCountry", "countries2.txt", true),
                            new AffiliationDictionaryFeature("KeywordInstitution", "institution_keywords.txt", false),
                            new AffiliationDictionaryFeature("KeywordState", "states.txt", true),
                            new AffiliationDictionaryFeature("KeywordStateCode", "state_codes.txt", true),
                            new AffiliationDictionaryFeature("KeywordStopWord", "stop_words_multilang.txt", false)
                    );

            WordFeatureCalculator wordFeature
                    = new WordFeatureCalculator(Arrays.<BinaryTokenFeatureCalculator>asList(
                            new IsNumberFeature()), false);

            EXTRACTOR = new AffiliationFeatureExtractor(binaryFeatures, keywordFeatures, wordFeature);
        } catch (AnalysisException e) {
            throw new RuntimeException("Failed to initialize the feature extractor");
        }
    }

    private class TokenContainer {

        public List<Token<AffiliationLabel>> tokens;
        public List<List<String>> features;

        public TokenContainer() {
            tokens = new ArrayList<Token<AffiliationLabel>>();
            features = new ArrayList<List<String>>();
        }

        public void add(String text, String... expectedFeatures) {
            tokens.add(new Token<AffiliationLabel>(text));
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

        tc.add("Maluwang", "W=Maluwang", "IsUpperCase");

        tc.add("Maluwang", "W=Maluwang", "IsUpperCase", "KeywordAddress");
        tc.add("na", "W=na", "KeywordAddress");
        tc.add("lansangan", "W=lansangan", "KeywordAddress");

        tc.add(".", "W=.", "IsSeparator");

        tc.add("les", "W=les");
        tc.add("escaldes", "W=escaldes");

        tc.add("les", "W=les", "KeywordCity");
        tc.add("Escaldes", "W=Escaldes", "KeywordCity", "IsUpperCase");

        tc.add("mhm", "W=mhm");

        tc.add("U", "W=U", "KeywordCountry", "IsUpperCase", "IsAllUpperCase");
        tc.add(".", "W=.", "IsSeparator", "KeywordCountry");
        tc.add("S", "W=S", "KeywordCountry", "IsUpperCase", "IsAllUpperCase");
        tc.add(".", "W=.", "IsSeparator", "KeywordCountry");
        tc.add("A", "W=A", "KeywordCountry", "IsUpperCase", "IsAllUpperCase");
        tc.add(".", "W=.", "IsSeparator", "KeywordCountry");

        tc.add("New", "W=New", "IsUpperCase", "KeywordState");
        tc.add("Hampshire", "W=Hampshire", "IsUpperCase", "KeywordState");

        tc.add("KS", "W=KS", "IsAllUpperCase", "KeywordStateCode"); // KS -- state code keyword

        tc.add("du", "W=du", "KeywordStopWord");

        DocumentAffiliation instance = new DocumentAffiliation("");
        instance.setTokens(tc.tokens);
        EXTRACTOR.calculateFeatures(instance);
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

        DocumentAffiliation instance = new DocumentAffiliation(text);
        instance.setTokens(TOKENIZER.tokenize(instance.getRawText()));
        EXTRACTOR.calculateFeatures(instance);
        for (int i = 0; i < expectedFeatures.size(); i++) {
            List<String> expected = expectedFeatures.get(i);
            List<String> actual = instance.getTokens().get(i).getFeatures();
            Collections.sort(expected);
            Collections.sort(actual);
            assertEquals(expected, actual);
        }
    }
}
