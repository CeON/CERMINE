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
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.model.DocumentSection;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ContentCitationPositionFinder {
    
    public ContentStructureCitationPositions findReferences(ContentStructure structure, List<BibEntry> citations) {
        List<Integer> indexes = new ArrayList<Integer>();
        Map<Integer, Integer> paragraphIndex = new HashMap<Integer, Integer>();
        Map<Integer, DocumentSection> sectionIndex = new HashMap<Integer, DocumentSection>();

        List<DocumentSection> sections = toSectionList(structure);
        StringBuilder sb = new StringBuilder();
        int length = 0;
        int index = 0;
        for (DocumentSection section : sections) {
            for (int i = 0; i < section.getParagraphs().size(); i++) {
                indexes.add(length);
                paragraphIndex.put(index, i);
                sectionIndex.put(index, section);
                sb.append(section.getParagraphs().get(i));
                length += section.getParagraphs().get(i).length();
                index++;
            }
        }

        String fullText = sb.toString();
        CitationPositionFinder finder = new CitationPositionFinder();
        List<List<CitationPosition>> positions = finder.findReferences(fullText, citations);
        
        ContentStructureCitationPositions contentPositions = new ContentStructureCitationPositions();
        
        for (int i = 0; i < positions.size(); i++) {
            List<CitationPosition> citationPositions = positions.get(i);
            for (CitationPosition pos : citationPositions) {
                int start = findIndex(pos.getStartRefPosition(), indexes);
                int end = findIndex(pos.getEndRefPosition(), indexes);
                if (start != end) {
                    continue;
                }
                CitationPosition shifted = new CitationPosition();
                shifted.setStartRefPosition(pos.getStartRefPosition()-indexes.get(start));
                shifted.setEndRefPosition(pos.getEndRefPosition()-indexes.get(start));
                contentPositions.addPosition(sectionIndex.get(start), paragraphIndex.get(start), i, shifted);
            }
        }
        return contentPositions;
    }
    
    private int findIndex(int i, List<Integer> indexes) {
        int prev = -1;
        for (int index : indexes) {
            if (index > i) {
                return prev;
            }
            prev++;
        }
        return prev;
    }
    
    private List<DocumentSection> toSectionList(ContentStructure structure) {
        List<DocumentSection> sections = new ArrayList<DocumentSection>();
        for (DocumentSection section : structure.getSections()) {
            addSections(sections, section);
        }
        return sections;
    }
    
    private void addSections(List<DocumentSection> sectionList, DocumentSection section) {
        sectionList.add(section);
        for (DocumentSection subsection : section.getSubsections()) {
            addSections(sectionList, subsection);
        }
    }

}
