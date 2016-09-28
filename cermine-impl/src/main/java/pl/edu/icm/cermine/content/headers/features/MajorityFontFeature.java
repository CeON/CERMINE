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

package pl.edu.icm.cermine.content.headers.features;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Jan Lasek
 */
public class MajorityFontFeature extends FeatureCalculator<BxLine, BxZone> {
    private final String mostPopularFont;

    public MajorityFontFeature(String mostPopularFont) {
        this.mostPopularFont = mostPopularFont;
    }
    
    @Override
    public double calculateFeatureValue(BxLine object, BxZone context) {
        String fn = object.getMostPopularFontName();
        if (mostPopularFont == null) {
            return 0;
        }
        return (fn.equals(mostPopularFont) ? 1 : 0);
    }

}
