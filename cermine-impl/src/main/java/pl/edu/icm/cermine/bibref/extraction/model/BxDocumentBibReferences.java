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

package pl.edu.icm.cermine.bibref.extraction.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxZone;

/**
 * Stores BxDocument's bibliographic references lines with their labels.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxDocumentBibReferences {

    private static final int MAX_TITLE_LENGTH = 30;
    
    /** A list of references' lines */
    private List<BxLine> lines = new ArrayList<BxLine>();

    /** A map associating references' line with their zones */
    private final Map<BxLine, BxZone> lineZones = new HashMap<BxLine, BxZone>();

    /** A map associating references' lines with their labels */
    private final Map<BxLine, BibReferenceLineLabel> lineLabels = new HashMap<BxLine, BibReferenceLineLabel>();

    public void addZone(BxZone zone) {
        for (BxLine line : zone) {
            String normalized = line.toText().toLowerCase().replaceAll("[^a-z]", "");
            if (line.toText().length() < MAX_TITLE_LENGTH && zone.getChild(0) == line && 
                    (normalized.startsWith("refer") || normalized.startsWith("biblio")
                    || normalized.startsWith("acknowled") || normalized.startsWith("conclus"))) {
                lines.clear();
                lineZones.clear();
                continue;
            }
            if (line.toText().replaceAll("[\\s\\u00A0]", "").isEmpty()) {
                continue;
            }
                   
            lines.add(line);
            lineZones.put(line, zone);
        }
    }

    public List<BxLine> getLines() {
        return lines;
    }
    
    public void limit(int limit) {
        if (lines.size() > limit) {
            lines = lines.subList(lines.size()-limit, lines.size());
        }
    }

    public BxZone getZone(BxLine line) {
        return lineZones.get(line);
    }

    public BibReferenceLineLabel getLabel(BxLine line) {
        return lineLabels.get(line);
    }

    public void setLabel(BxLine line, BibReferenceLineLabel label) {
        lineLabels.put(line, label);
    }

}
