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
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Pawel Szostek
 */
public class HorizontalRelativeProminenceFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        double leftProminence = zone.getX();
        double rightProminence = page.getWidth() - (zone.getX() + zone.getWidth());

        for (BxZone otherZone : page) {
            if (otherZone == zone) {
                continue;
            }
            double cx, cy, cw, ch, ox, oy, ow, oh;
            double newLeftProminence, newRightProminence;

            cx = zone.getBounds().getX();
            cy = zone.getBounds().getY();
            cw = zone.getBounds().getWidth();
            ch = zone.getBounds().getHeight();

            ox = otherZone.getBounds().getX();
            oy = otherZone.getBounds().getY();
            ow = otherZone.getBounds().getWidth();
            oh = otherZone.getBounds().getHeight();

            // Determine Octant
            //
            // 0 | 1 | 2
            // __|___|__
            // 7 | 9 | 3
            // __|___|__
            // 6 | 5 | 4

            int oct;
            if (cx + cw <= ox) {
                if (cy + ch <= oy) {
                    oct = 4;
                } else if (cy >= oy + oh) {
                    oct = 2;
                } else {
                    oct = 3;
                }
            } else if (ox + ow <= cx) {
                if (cy + ch <= oy) {
                    oct = 6;
                } else if (oy + oh <= cy) {
                    oct = 0;
                } else {
                    oct = 7;
                }
            } else if (cy + ch <= oy) {
                oct = 5;
            } else { // oy + oh <= cy
                oct = 1;
            }

            if (oct != 7 && oct != 3) {
                continue;
            }
            if (oct == 7) {
                newLeftProminence = Math.min(leftProminence, cx - (ox + ow));
                if (Math.abs(newLeftProminence - leftProminence) > Double.MIN_VALUE) {
                    leftProminence = newLeftProminence;
                }
            } else { //oct == 3
                newRightProminence = Math.min(rightProminence, ox - (cx + cw));
                if (Math.abs(newRightProminence - rightProminence) > Double.MIN_VALUE) {
                    rightProminence = newRightProminence;
                }
            }
        }
        double ret = (rightProminence + leftProminence + zone.getWidth()) / page.getWidth();
        if (ret >= 0.0) {
            return ret;
        } else {
            return 0.0;
        }
    }
};
