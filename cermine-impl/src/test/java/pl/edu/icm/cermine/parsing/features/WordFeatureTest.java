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
package pl.edu.icm.cermine.parsing.features;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.model.Token;

/**
 * @author Bartosz Tarnawski
 */
public class WordFeatureTest {

    @Test
    public void testComputeFeature() {
        String word = "BabaMaKota";
        String notWord = "123";

        List<Token<AffiliationLabel>> tokens = Arrays.asList(
                new Token<AffiliationLabel>(word),
                new Token<AffiliationLabel>(notWord)
        );

        WordFeatureCalculator instance = new WordFeatureCalculator(
                Arrays.<BinaryTokenFeatureCalculator>asList(new IsNumberFeature()), true);

        DocumentAffiliation aff = new DocumentAffiliation("");

        assertEquals("W=babamakota", instance.calculateFeatureValue(tokens.get(0), aff));
        assertNull(instance.calculateFeatureValue(tokens.get(1), aff));

        instance = new WordFeatureCalculator(
                Arrays.<BinaryTokenFeatureCalculator>asList(new IsNumberFeature()), false);
        assertEquals("W=BabaMaKota", instance.calculateFeatureValue(tokens.get(0), aff));
        assertNull(instance.calculateFeatureValue(tokens.get(1), aff));
    }

}
