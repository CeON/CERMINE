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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import com.google.common.collect.Lists;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PagesNumbersEnhancer extends AbstractFilterEnhancer {

    public PagesNumbersEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.GEN_OTHER, BxZoneLabel.MET_BIB_INFO);
        setSearchedFirstPageOnly(false);
    }

    @Override
    public void enhanceMetadata(BxDocument document, DocumentMetadata metadata, Set<EnhancedField> enhancedFields) {
        if (enhancedFields.contains(EnhancedField.PAGES)) {
            return;
        }
        List<BxPage> pages = Lists.newArrayList(document);
        
        Map<Integer, Set<Integer>> candidates = new HashMap<Integer, Set<Integer>>();
        for (int i = 0; i < pages.size(); i++) {
            candidates.put(i, new HashSet<Integer>());
            Iterator<BxZone> pageZones = this.filterZones(pages.get(i)).iterator();
            while (pageZones.hasNext()) {
                BxZone zone = pageZones.next();
                if (zone.toText().matches("^\\d{1,6}$")) {
                    int pageNumber = Integer.parseInt(zone.toText());
                    candidates.get(i).add(pageNumber);
                } else if (zone.childrenCount() == 1) {
                    Pattern p1 = Pattern.compile("^(\\d{1,6}).*$");
                    Matcher m1 = p1.matcher(zone.toText());
                    Pattern p2 = Pattern.compile("^.*(\\d{1,6})$");
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
            metadata.setPages(String.valueOf(bestarg), 
                            String.valueOf(bestarg + document.childrenCount()-1));
            enhancedFields.add(EnhancedField.PAGES);
        }

    }

}
