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
package pl.edu.icm.cermine.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class TextUtilsTest {

    @Test
    public void testIsWord() {
        String word = "BabamaKota";
        String notWord1 = "Baba ma";
        String notWord2 = "Baba123";
        assertTrue(TextUtils.isWord(word));
        assertFalse(TextUtils.isWord(notWord1));
        assertFalse(TextUtils.isWord(notWord2));
    }

    @Test
    public void testIsNumber() {
        String number = "53253325";
        String notNumber1 = "123 ";
        String notNumber2 = "123b";
        assertTrue(TextUtils.isNumber(number));
        assertFalse(TextUtils.isNumber(notNumber1));
        assertFalse(TextUtils.isNumber(notNumber2));
    }

    @Test
    public void testIsOnlyFirstUpperCase() {
        String firstUpperCase1 = "M";
        String firstUpperCase2 = "Mama";
        String notUpperCase = "aM";
        String moreUpperCase = "MM";
        String notWord = "Wombat1";
        assertTrue(TextUtils.isOnlyFirstUpperCase(firstUpperCase1));
        assertTrue(TextUtils.isOnlyFirstUpperCase(firstUpperCase2));
        assertFalse(TextUtils.isOnlyFirstUpperCase(notUpperCase));
        assertFalse(TextUtils.isOnlyFirstUpperCase(moreUpperCase));
        assertFalse(TextUtils.isOnlyFirstUpperCase(notWord));
    }

    @Test
    public void testAllUpperCase() {
        String firstUpperCase1 = "M";
        String firstUpperCase2 = "Mama";
        String notUpperCase = "aM";
        String moreUpperCase = "MM";
        String notWord = "W1";
        assertTrue(TextUtils.isAllUpperCase(firstUpperCase1));
        assertFalse(TextUtils.isAllUpperCase(firstUpperCase2));
        assertFalse(TextUtils.isAllUpperCase(notUpperCase));
        assertTrue(TextUtils.isAllUpperCase(moreUpperCase));
        assertFalse(TextUtils.isAllUpperCase(notWord));
    }

    @Test
    public void testIsSeparator() {
        String separator1 = ".";
        String separator2 = ",";
        String separator3 = ";";
        String notSeparator = ":";
        String twoSeparators = "..";
        assertTrue(TextUtils.isSeparator(separator1));
        assertTrue(TextUtils.isSeparator(separator2));
        assertTrue(TextUtils.isSeparator(separator3));
        assertFalse(TextUtils.isSeparator(notSeparator));
        assertFalse(TextUtils.isSeparator(twoSeparators));
    }

    @Test
    public void testIsNonAlphanumSep() {
        String alpha = "a";
        String num = "1";
        String sep = ",";
        String other = "(";
        assertFalse(TextUtils.isNonAlphanumSep(alpha));
        assertFalse(TextUtils.isNonAlphanumSep(num));
        assertFalse(TextUtils.isNonAlphanumSep(sep));
        assertTrue(TextUtils.isNonAlphanumSep(other));
    }

}
