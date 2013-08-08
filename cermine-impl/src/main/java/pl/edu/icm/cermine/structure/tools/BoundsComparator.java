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

package pl.edu.icm.cermine.structure.tools;

import java.util.Comparator;
import pl.edu.icm.cermine.structure.model.BxBounds;


/**
 *
 * @author estocka
 */
public class BoundsComparator implements Comparator {

    @Override
    public int compare(Object bounds1, Object bounds2) {
        int comparisonResult;
        double x1 = ((BxBounds)bounds1).getX();
        double x2 = ((BxBounds)bounds2).getX();
        comparisonResult = Double.compare(x1, x2);
        if(comparisonResult==0){
        double y1 = ((BxBounds)bounds1).getY();
        double y2 = ((BxBounds)bounds2).getY();
        comparisonResult = Double.compare(y1, y2);
        }

        return comparisonResult;
    }
}