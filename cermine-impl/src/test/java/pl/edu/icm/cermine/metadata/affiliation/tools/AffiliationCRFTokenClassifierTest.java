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
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.model.Token;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Bartosz Tarnawski
 */
public class AffiliationCRFTokenClassifierTest {

    private static final AffiliationTokenizer TOKENIZER = new AffiliationTokenizer();
    private static final AffiliationFeatureExtractor EXTRACTOR;

    static {
        try {
            EXTRACTOR = new AffiliationFeatureExtractor();
        } catch (AnalysisException e) {
            throw new RuntimeException("Failed to initialize the feature extractor");
        }
    }

    @Test
    public void testClassify() throws AnalysisException {
        List<Token<AffiliationLabel>> tokens = new ArrayList<Token<AffiliationLabel>>();
        for (int i = 0; i < 5; i++) {
            tokens.add(new Token<AffiliationLabel>());
        }
        tokens.get(0).setFeatures(Arrays.asList(
                "W=University",
                "IsUpperCase"
        ));
        tokens.get(1).setFeatures(Arrays.asList(
                "W=,",
                "IsSeparator"
        ));
        tokens.get(2).setFeatures(Arrays.asList(
                "W=Boston",
                "IsUpperCase",
                "KeywordCity"
        ));
        tokens.get(3).setFeatures(Arrays.asList(
                "W=,",
                "IsSeparator"
        ));
        tokens.get(4).setFeatures(Arrays.asList(
                "W=USA",
                "IsAllCapital",
                "KeywordCountry"
        ));
        new AffiliationCRFTokenClassifier().classify(tokens);

        for (Token<AffiliationLabel> token : tokens) {
            assertNotNull(token.getLabel());
        }
    }

    @Test
    public void testClassifyWithDocumentAffiliation() throws AnalysisException, TransformationException {
        String text = "Department of Oncology, Radiology and Clinical Immunology, Akademiska "
                + "Sjukhuset, Uppsala, Sweden";
        DocumentAffiliation instance = new DocumentAffiliation(text);
        instance.setTokens(TOKENIZER.tokenize(instance.getRawText()));
        EXTRACTOR.calculateFeatures(instance);
        new AffiliationCRFTokenClassifier().classify(instance.getTokens());
        assertEquals(AffiliationLabel.INST, instance.getTokens().get(0).getLabel());
        assertEquals(AffiliationLabel.INST, instance.getTokens().get(1).getLabel());
        assertEquals(AffiliationLabel.ADDR, instance.getTokens().get(12).getLabel());
        assertEquals(AffiliationLabel.COUN, instance.getTokens().get(14).getLabel());
    }

}
