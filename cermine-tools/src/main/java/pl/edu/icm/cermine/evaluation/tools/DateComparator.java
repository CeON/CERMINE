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
package pl.edu.icm.cermine.evaluation.tools;

import java.util.List;
import pl.edu.icm.cermine.tools.TextUtils;
import pl.edu.icm.cermine.tools.distance.CosineDistance;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DateComparator {

    public static Boolean yearsMatch(List<String> expected, List<String> extracted) {
        for (String expectedDate : expected) {
            List<String> expectedParts = TextUtils.tokenize(expectedDate);
            String expectedYear = null;
            for (String part : expectedParts) {
                if (TextUtils.isNumberBetween(part, 1900, 2100)) {
                    expectedYear = part;
                    break;
                }
            }
            if (expectedYear == null) {
                return null;
            } else {
                String extractedYear = null;
                for (String extractedDate : extracted) {
                    List<String> extractedParts = TextUtils.tokenize(extractedDate);
                    for (String part : extractedParts) {
                        if (part.length() == 4 && part.matches("^[0-9]+$")
                                && Integer.parseInt(part) < 2100 && Integer.parseInt(part) > 1900) {
                            extractedYear = part;
                            break;
                        }
                    }
                    if (extractedYear != null && extractedYear.equals(expectedYear)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Boolean datesMatch(List<String> expected, List<String> extracted) {
        Boolean anyExpectedOk = false;
        for (String expectedDate : expected) {
            List<String> expectedParts = TextUtils.tokenize(expectedDate);
            if (expectedParts.size() == 1) {
                continue;
            }
            anyExpectedOk = true;

            for (String extractedDate : extracted) {
                List<String> extractedParts = TextUtils.tokenize(extractedDate);
                if (extractedParts.size() == 1) {
                    continue;
                }
                if (new CosineDistance().compare(expectedParts, extractedParts) > 0.95) {
                    return true;
                }
            }
        }
        if (!anyExpectedOk) {
            return null;
        }
        return false;
    }
}
