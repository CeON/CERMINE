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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Pawel Szostek
 */
public class IsItemizeFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        String text = zone.toText();

        String itemizeString = "";
        itemizeString += "|^\\d+\\.\\d+\\.\\s+\\p{Upper}.+";
        itemizeString += "|^\\d+\\.\\s+\\p{Upper}.+";
        itemizeString += "|^\\p{Upper}\\.\\s[^\\.]+";
        itemizeString += "|^\\p{Lower}\\)\\s+.+";
        Pattern itemizePattern = Pattern.compile(itemizeString);

        String subpointsString = "";
        subpointsString += "^\\d\\.\\d\\.\\s+\\p{Upper}.+";
        subpointsString += "|^\\d\\.\\d\\.\\d\\.\\s+\\p{Upper}.+";
        Pattern subpointsPattern = Pattern.compile(subpointsString, Pattern.DOTALL); //for multiline matching

        Matcher matcher1 = itemizePattern.matcher(text);
        Matcher matcher2 = subpointsPattern.matcher(text);

        return (matcher1.matches() || matcher2.matches()) ? 1.0 : 0.0;
    }
}
