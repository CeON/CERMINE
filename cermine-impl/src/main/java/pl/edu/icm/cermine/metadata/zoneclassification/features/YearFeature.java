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
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.TextUtils;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class YearFeature extends FeatureCalculator<BxZone, BxPage> {

    private static final int MIN_YEAR = 1800;
    private static final int MAX_YEAR = 2100;
    
    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
 
        int yearCount = 0;
        for (BxLine line : zone) {
            
            String toMatch = line.toText();
            Pattern pattern = Pattern.compile("^\\D*(\\d+)(.*)$");
            while (Pattern.matches("^.*\\d.*", toMatch)) {
                Matcher matcher = pattern.matcher(toMatch);
                if (!matcher.matches()) {
                    break;
                }
                String numbers = matcher.group(1);
                if (TextUtils.isNumberBetween(numbers, MIN_YEAR, MAX_YEAR)) {
                    yearCount++;
                }
                toMatch = matcher.group(2);
            }
        }
        return (double)yearCount / (double)zone.childrenCount();
    }

}
