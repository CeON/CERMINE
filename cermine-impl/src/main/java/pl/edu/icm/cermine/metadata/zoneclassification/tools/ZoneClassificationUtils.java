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

import java.util.Map;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.tools.BxBoundsBuilder;

/**
 * Zone classification utility class.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ZoneClassificationUtils {

    private static final String[] CONJUNCTIONS = {"and", "or", "for", "or", "nor"};

    public static boolean isConjunction(String word) {
        for (String conjunction : CONJUNCTIONS) {
            if (conjunction.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }

    public static void mapZoneLabels(BxDocument document, Map<BxZoneLabel, BxZoneLabel> labelMap) {
        for (BxPage page : document) {
            for (BxZone zone : page) {
                if (labelMap.get(zone.getLabel()) != null) {
                    zone.setLabel(labelMap.get(zone.getLabel()));
                }
            }
        }
    }

    public static void correctPagesBounds(BxDocument document) {
        BxBoundsBuilder builder = new BxBoundsBuilder();
        for (BxPage page : document) {
            builder.expand(page.getBounds());
        }
        for (BxPage page : document) {
            page.setBounds(builder.getBounds());
        }
    }
}
