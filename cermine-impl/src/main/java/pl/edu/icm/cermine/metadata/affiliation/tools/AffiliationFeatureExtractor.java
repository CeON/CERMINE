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
import java.util.List;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliation.features.AffiliationDictionaryFeature;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.features.*;
import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.tools.FeatureExtractor;

/**
 * Feature extractor suitable for processing affiliations.
 *
 * @author Bartosz Tarnawski
 */
public class AffiliationFeatureExtractor implements FeatureExtractor<DocumentAffiliation> {

    private List<BinaryTokenFeatureCalculator> binaryFeatures;
    private List<KeywordFeatureCalculator<Token<AffiliationLabel>>> keywordFeatures;
    private WordFeatureCalculator wordFeature;

    @SuppressWarnings("unchecked")
    public AffiliationFeatureExtractor() throws AnalysisException {
        binaryFeatures
                = new ArrayList<BinaryTokenFeatureCalculator>(
                        Arrays.<BinaryTokenFeatureCalculator>asList(
                                new IsNumberFeature(),
                                new IsUpperCaseFeature(),
                                new IsAllUpperCaseFeature(),
                                new IsAllLowerCaseFeature()
                        ));

        keywordFeatures = Arrays.<KeywordFeatureCalculator<Token<AffiliationLabel>>>asList(
                new AffiliationDictionaryFeature("KeywordAddress", "address_keywords.txt", false),
                new AffiliationDictionaryFeature("KeywordCountry", "countries2.txt", true),
                new AffiliationDictionaryFeature("KeywordInstitution", "institution_keywords.txt", false)
        );

        wordFeature
                = new WordFeatureCalculator(Arrays.<BinaryTokenFeatureCalculator>asList(
                        new IsNumberFeature()), false);
    }

    /**
     * @param commonWords the words that are not considered 'Rare'
     * @throws AnalysisException AnalysisException
     */
    public AffiliationFeatureExtractor(List<String> commonWords) throws AnalysisException {
        this();
        binaryFeatures.add(new IsRareFeature(commonWords, true));
    }

    public AffiliationFeatureExtractor(List<BinaryTokenFeatureCalculator> binaryFeatures,
            List<KeywordFeatureCalculator<Token<AffiliationLabel>>> keywordFeatures,
            WordFeatureCalculator wordFeature) {
        this.binaryFeatures = binaryFeatures;
        this.keywordFeatures = keywordFeatures;
        this.wordFeature = wordFeature;
    }

    @Override
    public void calculateFeatures(DocumentAffiliation affiliation) {

        List<Token<AffiliationLabel>> tokens = affiliation.getTokens();
        for (Token<AffiliationLabel> token : tokens) {
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

        for (KeywordFeatureCalculator<Token<AffiliationLabel>> dictionaryFeatureCalculator
                : keywordFeatures) {
            dictionaryFeatureCalculator.calculateDictionaryFeatures(tokens);
        }
    }
}
