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

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.Utils;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Pawel Szostek
 */
public class IsHighestOnThePageFeature extends FeatureCalculator<BxZone, BxPage> {

    private static final double EPS = 10.0;

    private static class yCoordinateComparator implements Comparator<BxZone> {

        @Override
        public int compare(BxZone z1, BxZone z2) {
            return Utils.compareDouble(z1.getY() + z1.getHeight(), z2.getY() + z2.getHeight(), 0.1);
        }
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        List<BxZone> zones = Lists.newArrayList(page);
        Collections.sort(zones, new yCoordinateComparator());
        BxZone firstZone = zones.get(0);
        if (zone.equals(firstZone)) {
            return 1.0;
        } else if (Math.abs(zone.getY() - firstZone.getY()) <= EPS) {
            return 1.0;
        } else {
            return 0.0;
        }
    }
}
