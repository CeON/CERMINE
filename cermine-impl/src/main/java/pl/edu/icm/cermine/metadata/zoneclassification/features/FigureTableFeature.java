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

import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class FigureTableFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int i = 0;
        for (BxLine line : zone) {
            String text = line.toText().toLowerCase();
            if (text.matches("figure ?[0-9ivx]+[\\.:].*$") || text.matches("table ?[0-9ivx]+[\\.:].*$")
                    || text.matches("figure ?[0-9ivx]+$") || text.matches("table ?[0-9ivx]+$")) {
                if (i == 0) {
                    return 1;
                }
                if (Math.abs(line.getX() - line.getPrev().getX()) > 5) {
                    return 1;
                }
                double prevW = 0;
                for (BxWord w : line.getPrev()) {
                    for (BxChunk ch : w) {
                        prevW += ch.getArea();
                    }
                }
                prevW /= Math.max(line.getPrev().getArea(), line.getArea());
                double lineW = 0;
                for (BxWord w : line) {
                    for (BxChunk ch : w) {
                        prevW += ch.getArea();
                    }
                }
                lineW /= Math.max(line.getPrev().getArea(), line.getArea());
                if (Math.abs(lineW -prevW) < 0.3) {
                    return 1;
                }
                return 0.3;
            }
            i++;
        }
        return 0;
    }
    
}
