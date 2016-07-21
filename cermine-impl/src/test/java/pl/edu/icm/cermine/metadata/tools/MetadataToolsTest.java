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
package pl.edu.icm.cermine.metadata.tools;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Bartosz Tarnawski
 */
public class MetadataToolsTest {

    @Test
    public void testClean() {
        char ci = 0x0107; // LATIN SMALL LETTER C WITH ACUTE
        char c = 0x63; // LATIN SMALL LETTER C
        char acute = 0x301; // COMBINING ACUTE ACCENT
        String input = Character.toString(ci);
        String expected = Character.toString(c) + Character.toString(acute);
        String actual = MetadataTools.cleanAndNormalize(input);
        assertEquals(expected, actual);

        char dash = 0x2014; // EM_DASH
        input = Character.toString(dash);
        expected = "-";
        actual = MetadataTools.cleanAndNormalize(input);
        assertEquals(expected, actual);
    }
}
