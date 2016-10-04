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

package pl.edu.icm.cermine.content.citations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import pl.edu.icm.cermine.content.model.DocumentSection;
import pl.edu.icm.cermine.tools.Pair;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ContentStructureCitationPositions {

    private final Map<DocumentSection, Map<String, List<CitationPosition>>> positions =
            new HashMap<DocumentSection, Map<String, List<CitationPosition>>>();

    void addPosition(DocumentSection section, int paragraphIndex, int citationIndex, CitationPosition position) {
        if (positions.get(section) == null) {
            positions.put(section, new HashMap<String, List<CitationPosition>>());
        }
        String index = String.valueOf(paragraphIndex) + " " + String.valueOf(citationIndex);
        if (positions.get(section).get(index) == null) {
            positions.get(section).put(index, new ArrayList<CitationPosition>());
        }
        positions.get(section).get(index).add(position);
    }

    public List<Pair<Integer, CitationPosition>> getPositions(DocumentSection section, int index) {
        List<Pair<Integer, CitationPosition>> ret = new ArrayList<Pair<Integer, CitationPosition>>();
        Map<String, List<CitationPosition>> map = positions.get(section);
        if (map == null) {
            return ret;
        }
        for (Entry<String, List<CitationPosition>> entry : map.entrySet()) {
            int paragraphIndex = Integer.parseInt(entry.getKey().split(" ")[0]);
            int citationIndex = Integer.parseInt(entry.getKey().split(" ")[1]);
            if (paragraphIndex == index) {
                for (CitationPosition p : entry.getValue()) {
                    ret.add(new Pair<Integer, CitationPosition>(citationIndex, p));
                }
            }
        }
        return ret;
    }
    
}
