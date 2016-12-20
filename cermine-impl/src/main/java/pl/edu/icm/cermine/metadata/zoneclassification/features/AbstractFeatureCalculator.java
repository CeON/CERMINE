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

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Pawel Szostek
 * @param <S> object class
 * @param <T> context class
 */
public abstract class AbstractFeatureCalculator<S, T> extends FeatureCalculator<S, T> {

    protected static List<BxPage> getOtherPages(BxPage page) {
        List<BxPage> pages = new ArrayList<BxPage>();
        BxPage prevPage = page.getPrev();
        BxPage nextPage = page.getNext();

        while (prevPage != null) {
            pages.add(0, prevPage);
            prevPage = prevPage.getPrev();
        }
        while (nextPage != null) {
            pages.add(nextPage);
            nextPage = nextPage.getNext();
        }
        return pages;
    }

    protected static List<BxZone> getOtherZones(BxZone zone) {
        List<BxZone> zones = new ArrayList<BxZone>();
        BxZone prevZone = zone.getPrev();
        BxZone nextZone = zone.getNext();

        while (prevZone != null) {
            zones.add(0, prevZone);
            prevZone = prevZone.getPrev();
        }
        while (nextZone != null) {
            zones.add(nextZone);
            nextZone = nextZone.getNext();
        }
        return zones;
    }

}
