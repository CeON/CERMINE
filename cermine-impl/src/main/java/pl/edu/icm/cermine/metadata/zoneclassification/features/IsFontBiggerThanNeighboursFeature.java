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
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Pawel Szostek
 */
public class IsFontBiggerThanNeighboursFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        List<BxZone> pageZones = Lists.newArrayList(page);
        if (pageZones.isEmpty()) {
            return 0.0;
        }
        if (pageZones.size() == 1) {
            return 1.0;
        }

        Integer thisZoneIdx = null;
        for (int zoneIdx = 0; zoneIdx < pageZones.size(); ++zoneIdx) {
            if (zone == pageZones.get(zoneIdx)) {
                thisZoneIdx = zoneIdx;
                break;
            }
        }
        assert thisZoneIdx != null : "No zone in zone's context found";
        if (thisZoneIdx == 0) {
            FeatureCalculator<BxZone, BxPage> fontHeight = new FontHeightMeanFeature();
            double nextZoneFont = fontHeight.calculateFeatureValue(
                    pageZones.get(thisZoneIdx + 1), page);
            double thisZoneFont = fontHeight.calculateFeatureValue(
                    pageZones.get(thisZoneIdx), page);
            return thisZoneFont > nextZoneFont ? 1.0 : 0.0;
        } else if (thisZoneIdx == pageZones.size() - 1) {
            FeatureCalculator<BxZone, BxPage> fontHeight = new FontHeightMeanFeature();
            double prevZoneFont = fontHeight.calculateFeatureValue(
                    pageZones.get(thisZoneIdx - 1), page);
            double thisZoneFont = fontHeight.calculateFeatureValue(
                    pageZones.get(thisZoneIdx), page);
            return thisZoneFont > prevZoneFont ? 1.0 : 0.0;
        } else {
            FeatureCalculator<BxZone, BxPage> fontHeight = new FontHeightMeanFeature();
            double prevZoneFont = fontHeight.calculateFeatureValue(
                    pageZones.get(thisZoneIdx - 1), page);
            double thisZoneFont = fontHeight.calculateFeatureValue(
                    pageZones.get(thisZoneIdx), page);
            double nextZoneFont = fontHeight.calculateFeatureValue(
                    pageZones.get(thisZoneIdx + 1), page);

            return (thisZoneFont > prevZoneFont && thisZoneFont > nextZoneFont) ? 1.0
                    : 0.0;
        }
    }
}
