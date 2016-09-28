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

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.tools.MetadataTools;
import pl.edu.icm.cermine.parsing.model.Token;
import static org.junit.Assert.assertEquals;

/**
 * @author Bartosz Tarnawski
 */
public class AffiliationTokenizerTest {

    private final static AffiliationTokenizer TOKENIZER = new AffiliationTokenizer();

    @Test
    public void testTokenizeAscii() {
        String input = "ko  pi_es123_@@123Kot";

        List<Token<AffiliationLabel>> expected = Arrays.asList(
                new Token<AffiliationLabel>("ko", 0, 2),
                new Token<AffiliationLabel>("pi", 4, 6),
                new Token<AffiliationLabel>("_", 6, 7),
                new Token<AffiliationLabel>("es", 7, 9),
                new Token<AffiliationLabel>("123", 9, 12),
                new Token<AffiliationLabel>("_", 12, 13),
                new Token<AffiliationLabel>("@", 13, 14),
                new Token<AffiliationLabel>("@", 14, 15),
                new Token<AffiliationLabel>("123", 15, 18),
                new Token<AffiliationLabel>("Kot", 18, 21)
        );

        List<Token<AffiliationLabel>> actual = TOKENIZER.tokenize(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testTokenizeNonAscii() {
        String input = "śćdź óó";
        input = MetadataTools.cleanAndNormalize(input);

        List<Token<AffiliationLabel>> expected = Arrays.asList(
                new Token<AffiliationLabel>("scdz", 0, 7),
                new Token<AffiliationLabel>("oo", 8, 12)
        );

        List<Token<AffiliationLabel>> actual = TOKENIZER.tokenize(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testTokenizeWithDocumentAffiliation() {
        String text = "Cóż ro123bić?";
        List<Token<AffiliationLabel>> expected = Arrays.asList(
                new Token<AffiliationLabel>("Coz", 0, 5),
                new Token<AffiliationLabel>("ro", 6, 8),
                new Token<AffiliationLabel>("123", 8, 11),
                new Token<AffiliationLabel>("bic", 11, 15),
                new Token<AffiliationLabel>("?", 15, 16)
        );

        DocumentAffiliation instance = new DocumentAffiliation(text);

        List<Token<AffiliationLabel>> actual = TOKENIZER.tokenize(instance.getRawText());

        assertEquals(expected, actual);
    }

}
