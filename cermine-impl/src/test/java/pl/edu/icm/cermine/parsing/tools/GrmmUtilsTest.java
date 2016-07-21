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
package pl.edu.icm.cermine.parsing.tools;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.parsing.model.Token;

/**
 * @author Bartosz Tarnawski
 */
public class GrmmUtilsTest {

    @Test
    public void testToGrmmInput1() {
        String expected = "TEXT ---- F1 F2 F3";
        String actual = GrmmUtils.toGrmmInput("TEXT", Arrays.asList("F1", "F2", "F3"));
        assertEquals(expected, actual);
    }

    @Test
    public void testToGrmmInput2() {
        Token<AffiliationLabel> token1 = new Token<AffiliationLabel>("sometext");
        token1.setLabel(AffiliationLabel.TEXT);
        token1.getFeatures().add("F1");
        token1.getFeatures().add("F2");

        Token<AffiliationLabel> token2 = new Token<AffiliationLabel>("sometext2");
        token2.setLabel(AffiliationLabel.TEXT);
        token2.getFeatures().add("F3");

        String expected = "TEXT ---- F1 F2 Start@-1 F3@1\n"
                + "TEXT ---- F3 F1@-1 F2@-1 End@1\n";
        String actual = GrmmUtils.toGrmmInput(Arrays.asList(token1, token2), 1);
        assertEquals(expected, actual);
    }

}
