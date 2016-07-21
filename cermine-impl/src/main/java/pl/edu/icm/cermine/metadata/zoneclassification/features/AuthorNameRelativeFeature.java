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

package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;

/**
 * @author Pawel Szostek
 */
public class AuthorNameRelativeFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone object, BxPage context) {
        String text = object.toText();
        String[] parts = text.split(",|and");
        int numberOfNames = 0;
        if (parts.length == 0) {
            return 0;
        }
        for (String part : parts) {
            if (part.length() == 0) {
                ++numberOfNames;
                continue;
            }
            String[] words = part.split("\\s");

            boolean isName = true;
            for (String word : words) {
                if (word.length() == 1 && word.matches("\\*|")) {
                    continue;
                }
                if (word.length() == 2 && word.matches("\\w\\.")) {
                    continue;
                }
                if (word.matches("\\d+")) {
                    continue;
                }
                if (word.matches("\\p{Upper}.*")) {
                    continue;
                } else if (word.equals("van") || word.equals("von")) {
                    continue;
                }

                isName = false;
                break;
            }
            if (isName) {
                ++numberOfNames;
            }
        }
        return numberOfNames / (double) parts.length;
    }
}
