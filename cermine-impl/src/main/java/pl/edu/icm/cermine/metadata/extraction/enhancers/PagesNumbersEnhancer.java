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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 *
 * @author Dominika Tkaczyk
 */
public class PagesNumbersEnhancer extends AbstractFilterEnhancer {

    public PagesNumbersEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.GEN_OTHER);
        setSearchedFirstPageOnly(false);
    }

    @Override
    public void enhanceMetadata(BxDocument document, Element metadata, Set<EnhancedField> enhancedFields) {
        List<BxPage> pages = new ArrayList<BxPage>(document.getPages());
        int shift = 0;
        if (pages.size() >= 5) {
            pages.remove(pages.size() - 1);
            pages.remove(0);
            shift = -1;
        }
        Iterator<BxZone> firstPageZones = this.filterZones(pages.get(0)).iterator();
        while (firstPageZones.hasNext()) {
            String firstPageZone = firstPageZones.next().toText();
            if (firstPageZone.matches("^\\d+$")) {
                int firstPageNumber = Integer.parseInt(firstPageZone);
                boolean found = false;
                for (int i = 1; i < pages.size(); i++) {
                    found = false;
                    Iterator<BxZone> pageZones = this.filterZones(pages.get(i)).iterator();
                    while (pageZones.hasNext()) {
                        String pageZone = pageZones.next().toText();
                        if (!pageZone.matches("^\\d+$")) {
                            continue;
                        }
                        int pageNumber = Integer.parseInt(pageZone);
                        if (firstPageNumber + i == pageNumber) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        break;
                    }
                }
                if (found) {
                    Enhancers.setPages(metadata, String.valueOf(firstPageNumber+shift), 
                            String.valueOf(firstPageNumber+shift+document.asPages().size()-1));
                }
            }
        }
    }

}
