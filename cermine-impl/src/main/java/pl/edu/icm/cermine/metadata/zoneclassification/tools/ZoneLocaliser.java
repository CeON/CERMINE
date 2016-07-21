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
package pl.edu.icm.cermine.metadata.zoneclassification.tools;

import pl.edu.icm.cermine.structure.model.BxZone;

/**
 * @author Pawel Szostek
 */
public class ZoneLocaliser {

    private BxZone leftZone = null;
    private BxZone rightZone = null;
    private BxZone upperZone = null;
    private BxZone lowerZone = null;

    public ZoneLocaliser(BxZone zone) {
        for (BxZone otherZone : zone.getParent()) {
            if (otherZone == zone) {
                continue;
            }
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
            } else { // oy + oh <= cy
                oct = 1;
            }

            switch (oct) {
                case 1:
                    if (upperZone == null || otherZone.getY() + otherZone.getHeight() > upperZone.getY() + upperZone.getHeight()) {
                        upperZone = otherZone;
                    }   break;
                case 5:
                    if (lowerZone == null || otherZone.getY() < lowerZone.getY()) {
                        lowerZone = otherZone;
                    }   break;
                case 7:
                    if (leftZone == null || otherZone.getX() + otherZone.getWidth() > leftZone.getX() + leftZone.getWidth()) {
                        leftZone = otherZone;
                    }   break;
                case 3:
                    if (rightZone == null || otherZone.getX() < rightZone.getX()) {
                        rightZone = otherZone;
                    }   break;
                default:
                    break;
            }
        }
    }

    public BxZone getLeftZone() {
        return leftZone;
    }

    public BxZone getRightZone() {
        return rightZone;
    }

    public BxZone getUpperZone() {
        return upperZone;
    }

    public BxZone getLowerZone() {
        return lowerZone;
    }

}
