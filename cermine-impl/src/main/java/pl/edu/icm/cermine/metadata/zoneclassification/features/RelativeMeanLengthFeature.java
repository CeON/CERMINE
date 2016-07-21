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

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class RelativeMeanLengthFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        BxLine firstLine = zone.getFirstChild();
        BxLine line = firstLine;
        double meanTotalWidth = line.getWidth();
        int lineCount = 1;
        
        while (line.hasPrev()) {
            line = line.getPrev();
            meanTotalWidth += line.getWidth();
            lineCount++;
        }
        line = firstLine;
        while (line.hasNext()) {
            line = line.getNext();
            meanTotalWidth += line.getWidth();
            lineCount++;
        }
        
        if (lineCount == 0) {
            return 0;
        }
        
        meanTotalWidth /= lineCount;
        
        double meanZoneWidth = 0;
        for (BxLine l : zone) {
            meanZoneWidth += l.getWidth();
        }
        
        if (!zone.hasChildren() || meanTotalWidth == 0) {
            return 0;
        }
        
        return meanZoneWidth / zone.childrenCount() / meanTotalWidth;
    }
    
}
