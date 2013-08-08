/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.*;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * Merged title enhancer.
 * 
 * @author krusek
 */
public class TitleEnhancer extends AbstractSimpleEnhancer {

    public TitleEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_TITLE);
        setSearchedFirstPageOnly(true);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.TITLE);
    }

    @Override
    protected boolean enhanceMetadata(BxPage page, Element metadata) {
        List<BxZone> titleZones = new ArrayList<BxZone>();
        for (BxZone zone : filterZones(page)) {
            titleZones.add(zone);
        }
        Collections.sort(titleZones, new Comparator<BxZone>() {

            @Override
            public int compare(BxZone t1, BxZone t2) {
                return Double.compare(t2.getLines().get(0).getBounds().getHeight(),
                        t1.getLines().get(0).getBounds().getHeight());
            }

        });

        if (!titleZones.isEmpty()) {
            BxZone titleZone = titleZones.get(0);
            double height = titleZone.getLines().get(0).getHeight();
            while (titleZone.hasPrev() 
                    && BxZoneLabel.MET_TITLE.equals(titleZone.getPrev().getLabel())
                    && Math.abs(height-titleZone.getPrev().getLines().get(0).getHeight()) < 0.5) {
                titleZone = titleZone.getPrev();
            }
            
            StringBuilder titleSB = new StringBuilder(titleZone.toText());
            while (titleZone.hasNext() 
                    && BxZoneLabel.MET_TITLE.equals(titleZone.getNext().getLabel())
                    && Math.abs(height-titleZone.getNext().getLines().get(0).getHeight()) < 0.5) {
                titleZone = titleZone.getNext();
                titleSB.append(" ");
                titleSB.append(titleZone.toText());
            }

            if (!titleSB.toString().isEmpty()) {
                Enhancers.setTitle(metadata, titleSB.toString().trim().replaceAll("\n", " "));
                return true;
            }
        }
        return false;
    }
}
