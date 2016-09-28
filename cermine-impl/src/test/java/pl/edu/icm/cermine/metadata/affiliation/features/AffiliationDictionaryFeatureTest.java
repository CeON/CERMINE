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
package pl.edu.icm.cermine.metadata.affiliation.features;

import java.util.List;
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.tools.MetadataTools;
import pl.edu.icm.cermine.parsing.model.Token;
import static org.junit.Assert.assertEquals;

/**
 * @author Bartosz Tarnawski
 */
public class AffiliationDictionaryFeatureTest {

    private static final AffiliationDictionaryFeature FEATURE_CASE_SENSITIVE;
    private static final AffiliationDictionaryFeature FEATURE_IGNORE_CASE;

    static {
        try {
            FEATURE_CASE_SENSITIVE = new AffiliationDictionaryFeature("HIT", "mock-dictionary.txt",
                    true);
            FEATURE_IGNORE_CASE = new AffiliationDictionaryFeature("HIT", "mock-dictionary.txt",
                    false);
        } catch (AnalysisException e) {
            throw new RuntimeException("Failed to initialize dictionary features");
        }
    }

    @Test
    public void testAddFeatures() {
        String text = "elo meLo  320, Elo Melo 32.0. Hejka ziomeczku,:-P. W W chrzaszczu szczebrzeszyn ..";
        int expectFeature[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
        List<Token<AffiliationLabel>> tokens
                = new AffiliationTokenizer().tokenize(MetadataTools.cleanAndNormalize(text));
        assertEquals(expectFeature.length, tokens.size());
        FEATURE_CASE_SENSITIVE.calculateDictionaryFeatures(tokens);

        for (int i = 0; i < expectFeature.length; i++) {
            assertEquals("Token: " + i + " " + tokens.get(i).getText(), expectFeature[i],
                    tokens.get(i).getFeatures().size());
        }

        text = "elo meLo  320, Elo Melo 32.0. Hejka ziómeczku,:-P. W W chrzaszczu szczebrzeszyn ..";
        int expectFeature2[] = {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0};
        tokens = new AffiliationTokenizer().tokenize(MetadataTools.cleanAndNormalize(text));
        assertEquals(expectFeature2.length, tokens.size());
        FEATURE_IGNORE_CASE.calculateDictionaryFeatures(tokens);

        for (int i = 0; i < expectFeature2.length; i++) {
            assertEquals("Token: " + i + " " + tokens.get(i).getText(), expectFeature2[i],
                    tokens.get(i).getFeatures().size());
        }
    }

}
