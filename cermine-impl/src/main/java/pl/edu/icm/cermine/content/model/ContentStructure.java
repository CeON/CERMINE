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

package pl.edu.icm.cermine.content.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ContentStructure {

    private List<DocumentSection> sections = new ArrayList<DocumentSection>();

    public List<DocumentSection> getSections() {
        return getSections(false);
    }
    
    public List<DocumentSection> getSections(boolean recursive) {
        if (!recursive) {
            return sections;
        }
        List<DocumentSection> secs = new ArrayList<DocumentSection>();
        for (DocumentSection sec : sections) {
            secs.add(sec);
            secs.addAll(sec.getSubsections(true));
        }
        return secs;
    }

    public void setSections(List<DocumentSection> sections) {
        this.sections = sections;
    }

    public void addSection(DocumentSection section) {
        sections.add(section);
    }
    
}
