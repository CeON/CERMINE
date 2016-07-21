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
package pl.edu.icm.cermine.parsing.model;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;

/**
 * @author Bartosz Tarnawski
 */
public class TokenTest {

    @Test
    public void testSequenceEquals() {
        List<Token<AffiliationLabel>> sequence1 = Arrays.asList(
                new Token<AffiliationLabel>("Bob", 1, 23),
                new Token<AffiliationLabel>("Hop", 1, 23)
        );
        List<Token<AffiliationLabel>> sequence2 = Arrays.asList(
                new Token<AffiliationLabel>("bob", 19, 23),
                new Token<AffiliationLabel>("hoP", 11, 87)
        );
        List<Token<AffiliationLabel>> sequence3 = Arrays.asList(
                new Token<AffiliationLabel>("Bobb", 1, 23),
                new Token<AffiliationLabel>("Hop", 1, 23)
        );
        List<Token<AffiliationLabel>> sequence4 = Arrays.asList(
                new Token<AffiliationLabel>("Bob", 19, 23)
        );
        assertTrue(Token.sequenceTextEquals(sequence1, sequence2, false));
        assertFalse(Token.sequenceTextEquals(sequence1, sequence2, true));
        assertFalse(Token.sequenceTextEquals(sequence1, sequence3, false));
        assertFalse(Token.sequenceTextEquals(sequence1, sequence4, false));
    }

}
