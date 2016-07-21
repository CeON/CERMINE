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
public class DistanceFromNearestNeighbourFeature extends FeatureCalculator<BxZone, BxPage> {

    private static double euclideanDist(double x0, double y0, double x1, double y1) {
        return Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        double minDist = Double.MAX_VALUE;

        for (BxZone otherZone : page) {
            if (otherZone == zone) {
                continue;
            }

            double dist;
            double cx, cy, cw, ch, ox, oy, ow, oh;
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
            } else if (oy + oh <= cy) {
                oct = 1;
            } else {
                continue;
            }
            // determine distance based on octant
            switch (oct) {
                case 0:
                    dist = euclideanDist(ox + ow, oy + oh, cx, cy);
                    break;
                case 1:
                    dist = cy - (oy + oh);
                    break;
                case 2:
                    dist = euclideanDist(ox, oy + oh, cx + cw, cy);
                    break;
                case 3:
                    dist = ox - (cx + cw);
                    break;
                case 4:
                    dist = euclideanDist(cx + cw, cy + ch, ox, oy);
                    break;
                case 5:
                    dist = oy - (cy + ch);
                    break;
                case 6:
                    dist = euclideanDist(ox + ow, oy, cx, cx + ch);
                    break;
                case 7:
                    dist = cx - (ox + ow);
                    break;
                default:
                    dist = Double.MAX_VALUE;
            }
            
            if (dist < minDist) {
                minDist = dist;
            }
        }
        if (minDist == Double.MAX_VALUE) {
            return 0.0;
        } else {
            return minDist;
        }
    }
};
