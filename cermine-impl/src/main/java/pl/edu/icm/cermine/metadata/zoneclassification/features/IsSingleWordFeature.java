/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

public class IsSingleWordFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone object, BxPage context) {
        String text = object.toText();
        if (text.length() == 0) //zone is empty
        {
            return 0.0;
        }
        String[] parts = text.split(" \\n\\t");
        if (parts.length == 1) { //white characters were not found
            return 1.0;
        }
        Boolean foundNonEmpty = false;
        for (String part : parts) {
            if (part.length() == 0) { //empty string => ommit
                continue;
            } else {
                if (foundNonEmpty) { //already found a non-empty stting => there are many words
                    return 0.0;
                } else { //mark that a non-empty string was found
                    foundNonEmpty = true;
                }
            }
        }
        return 1.0;
    }
}
