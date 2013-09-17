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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        setSearchedZoneLabels(BxZoneLabel.GEN_OTHER, BxZoneLabel.MET_BIB_INFO);
        setSearchedFirstPageOnly(false);
    }

    @Override
    public void enhanceMetadata(BxDocument document, Element metadata, Set<EnhancedField> enhancedFields) {
        if (enhancedFields.contains(EnhancedField.PAGES)) {
            return;
        }
        List<BxPage> pages = new ArrayList<BxPage>(document.getPages());
        
        Map<Integer, Set<Integer>> candidates = new HashMap<Integer, Set<Integer>>();
        for (int i = 0; i < pages.size(); i++) {
            candidates.put(i, new HashSet<Integer>());
            Iterator<BxZone> pageZones = this.filterZones(pages.get(i)).iterator();
            while (pageZones.hasNext()) {
                BxZone zone = pageZones.next();
                if (zone.toText().matches("^\\d+$")) {
                    int pageNumber = Integer.parseInt(zone.toText());
                    candidates.get(i).add(pageNumber);
                } else if (zone.getLines().size() == 1) {
                    Pattern p1 = Pattern.compile("^(\\d+).*$");
                    Matcher m1 = p1.matcher(zone.toText());
                    Pattern p2 = Pattern.compile("^.*(\\d+)$");
                    Matcher m2 = p2.matcher(zone.toText());
                    if (m1.matches()) {
                        int pageNumber = Integer.parseInt(m1.group(1));
                        candidates.get(i).add(pageNumber);
                    }
                    if (m2.matches()) {
                        int pageNumber = Integer.parseInt(m2.group(1));
                        candidates.get(i).add(pageNumber);
                    }
                }
            }
        }
        
        Map<Integer, Integer> candidates1 = new HashMap<Integer, Integer>();
        for (int i = 0; i < pages.size(); i++) {
            Set<Integer> actCandidates = candidates.get(i);
            for (Integer actCand : actCandidates) {
                int mine = actCand - i;
                int ile = 1;
                for (int j = i+1; j < pages.size(); j++) {
                    if (candidates.get(j).contains(mine+j)) {
                        candidates.get(j).remove(mine+j);
                        ile++;
                    }
                }
                if (mine > 1) {
                    candidates1.put(mine, ile);
                }
            }
        }
        
        int best = -1;
        int bestarg = -1;
        for (int index : candidates1.keySet()) {
            if (candidates1.get(index) > best && candidates1.get(index) >= 2) {
                bestarg = index;
                best = candidates1.get(index);
            }
        }
        
        if (best > -1) {
            Enhancers.setPages(metadata, String.valueOf(bestarg), 
                            String.valueOf(bestarg+document.asPages().size()-1));
            enhancedFields.add(EnhancedField.PAGES);
        }

    }

}
