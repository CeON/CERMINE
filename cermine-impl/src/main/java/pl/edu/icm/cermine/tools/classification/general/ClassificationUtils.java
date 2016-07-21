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

package pl.edu.icm.cermine.tools.classification.general;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;

/**
 * @author Pawel Szostek
 */
public class ClassificationUtils {

    public static List<TrainingSample<BxZoneLabel>> filterElements(List<TrainingSample<BxZoneLabel>> elements, BxZoneLabelCategory category) {
        if (category == BxZoneLabelCategory.CAT_ALL) {
            List<TrainingSample<BxZoneLabel>> ret = new ArrayList<TrainingSample<BxZoneLabel>>();
            ret.addAll(elements);
            return ret;
        }

        List<TrainingSample<BxZoneLabel>> ret = new ArrayList<TrainingSample<BxZoneLabel>>();
        
        for (TrainingSample<BxZoneLabel> elem : elements) {
        	if (elem.getLabel() == null) {
        		continue;
        	}
            if (elem.getLabel().getCategory() == category) {
                ret.add(elem);
            }
        }
        return ret;
    }

    public static List<TrainingSample<BxZoneLabel>> filterElements(List<TrainingSample<BxZoneLabel>> elements, List<BxZoneLabel> labels) {

        List<TrainingSample<BxZoneLabel>> ret = new ArrayList<TrainingSample<BxZoneLabel>>();

        for (TrainingSample<BxZoneLabel> elem : elements) {
            if (labels.contains(elem.getLabel())) {
                ret.add(elem);
            }
        }
        return ret;
    }
}
