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
public class VerticalProminenceFeature extends FeatureCalculator<BxZone, BxPage> {

    private static final double ZONE_EPSILON = 1.0;

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        if (page.childrenCount() == 1) {
            return 0.0; //there is only one zone - no prominence can be measured
        }
        BxZone prevZone = zone.getPrev();
        BxZone nextZone = zone.getNext();
        while (true) {
            if (prevZone == null) { //given zone is the first one in the set - there is none before it
                if (nextZone == null) {
                    return page.getHeight() - zone.getHeight();
                } else if (nextZone.getY() - (zone.getY() + zone.getHeight()) > ZONE_EPSILON) {
                    return nextZone.getY() - (zone.getY() + zone.getHeight());
                } else {
                    nextZone = nextZone.getNext();
                }
            } else if (nextZone == null) { //given zone is the last one in the set - there is none after it
                if (zone.getY() - (prevZone.getY() + prevZone.getHeight()) > ZONE_EPSILON) {
                    return zone.getY() - (prevZone.getY() + prevZone.getHeight());
                } else {
                    prevZone = prevZone.getPrev();
                }
            } else { //there is a zone before and after the given one
                if (zone.getY() - (prevZone.getY() + prevZone.getHeight()) > ZONE_EPSILON) { //previous zone lies in the same column
                    if (nextZone.getY() - (zone.getY() + zone.getHeight()) > ZONE_EPSILON) { //next zone lies in the same column
                        return nextZone.getY() - (prevZone.getY() + prevZone.getHeight()) - zone.getHeight();
                    } else {
                        nextZone = nextZone.getNext();
                    }
                } else {
                    if (nextZone.getY() - (zone.getY() + zone.getHeight()) > ZONE_EPSILON) {
                        prevZone = prevZone.getPrev();
                    } else { //neither previous zone nor next zone lies in natural geometrical order
                        prevZone = prevZone.getPrev();
                        nextZone = nextZone.getNext();
                    }
                }
            }
        }
    }
}
