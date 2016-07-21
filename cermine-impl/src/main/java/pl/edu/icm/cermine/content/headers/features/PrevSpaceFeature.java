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
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PrevSpaceFeature extends FeatureCalculator<BxLine, BxPage> {

    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        if (!line.hasPrev() || line.getPrev().getY() > line.getY()) {
            return 0;
        }
        
        double space = line.getY() - line.getPrev().getY();
        
        BxLine l = line;
        int i = 0;
        while (l.hasPrev()) {
            l = l.getPrev();
            if (!l.hasPrev()) {
                break;
            }
            if (i >= 4 || l.getPrev().getY() > l.getY()) {
                break;
            }
            if (l.getY() - l.getPrev().getY() > space) {
                space = l.getY() - l.getPrev().getY();
            }
            i++;
        }
                
        return (Math.abs(space - line.getY() + line.getPrev().getY()) < 0.1) ? 1 : 0;
    }
    
}
