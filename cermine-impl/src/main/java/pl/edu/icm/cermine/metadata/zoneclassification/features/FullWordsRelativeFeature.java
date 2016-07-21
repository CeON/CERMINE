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

import pl.edu.icm.cermine.metadata.zoneclassification.tools.ZoneClassificationUtils;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;

/**
 * @author Pawel Szostek
 */
public class FullWordsRelativeFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone object, BxPage context) {
        String text = object.toText();
        String[] words = text.split("\\s");
        int numberOfWords = 0;
        int numberOfFullWords = 0;
        for (String word : words) {
            if (ZoneClassificationUtils.isConjunction(word)) {
                ++numberOfFullWords;
            } else if (word.length() > 2 && !word.matches(".*\\d.*") && !word.matches(".*[^\\p{Alnum}].*")) {
                ++numberOfFullWords;
            }
            ++numberOfWords;
        }
        return (double) numberOfFullWords / numberOfWords;
    }

}
