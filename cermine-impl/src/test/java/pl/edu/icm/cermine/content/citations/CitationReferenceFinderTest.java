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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import pl.edu.icm.cermine.bibref.model.BibEntryFieldType;
import pl.edu.icm.cermine.bibref.model.BibEntryType;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CitationReferenceFinderTest {
    
    private static final String DOCUMENT_TEXT1 = 
        "This is a state of the art fragment with absolutely no references whatsoever.";

    private static final String DOCUMENT_TEXT2 = 
        "This is a typical state of the art fragment. We can reference a single document " +
        "like this [2] or [ 12].";

    private static final String DOCUMENT_TEXT3 = 
        "This is a typical state of the art fragment. Sometimes we use [3,2, 4, 12 ] to " +
        "reference multiple documents in one place.";

    private static final String DOCUMENT_TEXT4 = 
        "This is a typical state of the art fragment. To save space, the number can also " +
        "be given as ranges: [2-4] or [1-5, 7].";
    
    private static final String DOCUMENT_TEXT5 =
        "This is a typical state of the art fragment. Reference can also be given by " +
        "author name (Hoeffding, 1963), multiple refs: (Agranovitch and Vishisk, 1964), " +
        "also multiple references: (Lee et al., 2004; Agranovitch and Vishisk, 1964; " +
        "Wang et al., 2003)";
    
    private static final String DOCUMENT_TEXT6 =
        "This is a typical state of the art fragment. We can reference a single document " +
        "like this (2) or ( 1). Sometimes we use (3,2, 4, 12 ) to reference multiple " +
        "documents in one place. To save space, the number can also be given as ranges: " +
        "(2-4) or (1-5, 7).";
    
    private static final BibEntry[] CITATIONS = {
        new BibEntry().setText(" [12]  W. Hoeffding, Probability inequalities for sums of bounded random variables, J. Amer. Statist. Assoc, 58 (1963) 13-30.")
            .addField(BibEntryFieldType.AUTHOR, "Hoeffding, W.")
            .addField(BibEntryFieldType.TITLE, "Probability inequalities for sums of bounded random variables")
            .addField(BibEntryFieldType.JOURNAL, "J. Amer. Statist. Assoc")
            .addField(BibEntryFieldType.VOLUME, "58")
            .addField(BibEntryFieldType.YEAR, "1963")
            .addField(BibEntryFieldType.PAGES, "13--30"),
        new BibEntry().setText("[2]  Agranovitch (M.S.) and  Vishisk (M.I.). — Elliptic problems with a parameter and parabolic problems of general type, Russian Math. Surveys, 19, 1964, 53-157.")
            .addField(BibEntryFieldType.AUTHOR, "Agranovitch, M.S.")
            .addField(BibEntryFieldType.AUTHOR, "Vishisk, M.I.")
            .addField(BibEntryFieldType.TITLE, "Elliptic problems with a parameter and parabolic problems of general type")
            .addField(BibEntryFieldType.JOURNAL, "Russian Math. Surveys")
            .addField(BibEntryFieldType.VOLUME, "19")
            .addField(BibEntryFieldType.YEAR, "1964")
            .addField(BibEntryFieldType.PAGES, "53--157"),
        new BibEntry().setText("5.  M-Y. Wang,  X. Wang and  D. Guo, A level-set method for structural topology optimization. Comput. Methods Appl. Mech. Engrg, 192 (2003) 227–246.")
            .addField(BibEntryFieldType.AUTHOR, "Wang, M-Y.")
            .addField(BibEntryFieldType.AUTHOR, "Wang, X.")
            .addField(BibEntryFieldType.AUTHOR, "Guo, D.")
            .addField(BibEntryFieldType.TITLE, "A level-set method for structural topology optimization")
            .addField(BibEntryFieldType.JOURNAL, "Comput. Methods Appl. Mech. Engrg")
            .addField(BibEntryFieldType.VOLUME, "192")
            .addField(BibEntryFieldType.YEAR, "2003")
            .addField(BibEntryFieldType.PAGES, "227--246"),
        new BibEntry().setText("  [4] R. Kobayashi, Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities, Math. Ann. 272 (1985), 385-398.")
            .addField(BibEntryFieldType.AUTHOR, "Kobayashi, R.")
            .addField(BibEntryFieldType.TITLE, "Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities")
            .addField(BibEntryFieldType.JOURNAL, "Math. Ann.")
            .addField(BibEntryFieldType.VOLUME, "272")
            .addField(BibEntryFieldType.YEAR, "1985")
            .addField(BibEntryFieldType.PAGES, "385--398"),
        new BibEntry(BibEntryType.ARTICLE)
            .setText("7. W. C. Lee, Y. E. Chavez, T. Baker, and B. R. Luce, “Economic burden of heart failure: a summary of recent literature,” Heart and Lung, vol. 33, no. 6, pp. 362–371, 2004.")
            .addField(BibEntryFieldType.AUTHOR, "Lee, W. C.")
            .addField(BibEntryFieldType.AUTHOR, "Chavez, Y. E.")
            .addField(BibEntryFieldType.AUTHOR, "Baker, T.")
            .addField(BibEntryFieldType.AUTHOR, "Luce, B. R.")
            .addField(BibEntryFieldType.TITLE, "Economic burden of heart failure: a summary of recent literature")
            .addField(BibEntryFieldType.JOURNAL, "Heart and Lung")
            .addField(BibEntryFieldType.VOLUME, "33")
            .addField(BibEntryFieldType.NUMBER, "6")
            .addField(BibEntryFieldType.YEAR, "2004")
            .addField(BibEntryFieldType.PAGES, "362--371"),
        
    };

    @Test
    public void testReferenceFinderNoRefs() {
        CitationPositionFinder finder = new CitationPositionFinder();
        
        List<Set<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT1, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());
        for (Set<CitationPosition> pos : positions) {
            assertTrue(pos.isEmpty());
        }
    }

    @Test
    public void testReferenceFinderSingleRefs() {
        CitationPositionFinder finder = new CitationPositionFinder();
        
        List<Set<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT2, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());

        assertEquals(1, positions.get(0).size());
        assertEquals(98, positions.get(0).iterator().next().getStartRefPosition());
        assertEquals(101, positions.get(0).iterator().next().getEndRefPosition());
        
        assertEquals(1, positions.get(1).size());
        assertEquals(91, positions.get(1).iterator().next().getStartRefPosition());
        assertEquals(92, positions.get(1).iterator().next().getEndRefPosition());
    }
    
    @Test
    public void testReferenceFinderMultipleRefs() {
        CitationPositionFinder finder = new CitationPositionFinder();
        
        List<Set<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT3, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());
        
        assertEquals(1, positions.get(0).size());
        assertEquals(63, positions.get(0).iterator().next().getStartRefPosition());
        assertEquals(74, positions.get(0).iterator().next().getEndRefPosition());
        
        assertEquals(1, positions.get(1).size());
        assertEquals(63, positions.get(1).iterator().next().getStartRefPosition());
        assertEquals(74, positions.get(1).iterator().next().getEndRefPosition());
        
        assertEquals(1, positions.get(3).size());
        assertEquals(63, positions.get(3).iterator().next().getStartRefPosition());
        assertEquals(74, positions.get(3).iterator().next().getEndRefPosition());
    }
    
    @Test
    public void testReferenceFinderRangeRefs() {
        CitationPositionFinder finder = new CitationPositionFinder();
        
        List<Set<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT4, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());
        
        List<CitationPosition> pos1 = toSortedList(positions.get(1));
        assertEquals(2, pos1.size());
        assertEquals(101, pos1.get(0).getStartRefPosition());
        assertEquals(104, pos1.get(0).getEndRefPosition());
        assertEquals(110, pos1.get(1).getStartRefPosition());
        assertEquals(116, pos1.get(1).getEndRefPosition());

        List<CitationPosition> pos3 = toSortedList(positions.get(3));
        assertEquals(2, pos3.size());
        assertEquals(101, pos3.get(0).getStartRefPosition());
        assertEquals(104, pos3.get(0).getEndRefPosition());
        assertEquals(110, pos3.get(1).getStartRefPosition());
        assertEquals(116, pos3.get(1).getEndRefPosition());
        
        List<CitationPosition> pos4 = toSortedList(positions.get(4));
        assertEquals(1, pos4.size());
        assertEquals(110, pos4.get(0).getStartRefPosition());
        assertEquals(116, pos4.get(0).getEndRefPosition());
    }
    
    @Test
    public void testReferenceFinderAuthorNames() {
        CitationPositionFinder finder = new CitationPositionFinder();
    
        List<Set<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT5, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());
        
        List<CitationPosition> pos0 = toSortedList(positions.get(0));
        assertEquals(1, pos0.size());
        assertEquals(88, pos0.get(0).getStartRefPosition());
        assertEquals(105, pos0.get(0).getEndRefPosition());
     
        List<CitationPosition> pos1 = toSortedList(positions.get(1));
        assertEquals(2, pos1.size());
        assertEquals(122, pos1.get(0).getStartRefPosition());
        assertEquals(153, pos1.get(0).getEndRefPosition());
        assertEquals(181, pos1.get(1).getStartRefPosition());
        assertEquals(249, pos1.get(1).getEndRefPosition());
       
        List<CitationPosition> pos2 = toSortedList(positions.get(2));
        assertEquals(1, pos2.size());
        assertEquals(181, pos2.get(0).getStartRefPosition());
        assertEquals(249, pos2.get(0).getEndRefPosition());
        
        assertEquals(0, positions.get(3).size());
        
        List<CitationPosition> pos4 = toSortedList(positions.get(4));
        assertEquals(1, pos4.size());
        assertEquals(181, pos4.get(0).getStartRefPosition());
        assertEquals(249, pos4.get(0).getEndRefPosition());
    }
    
    @Test
    public void testReferenceFinderRoundBrackets() {
        CitationPositionFinder finder = new CitationPositionFinder();
    
        List<Set<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT6, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());
        
        List<CitationPosition> pos0 = toSortedList(positions.get(0));
        assertEquals(1, pos0.size());
        assertEquals(121, pos0.get(0).getStartRefPosition());
        assertEquals(132, pos0.get(0).getEndRefPosition());
     
        List<CitationPosition> pos1 = toSortedList(positions.get(1));
        assertEquals(4, pos1.size());
        assertEquals(91, pos1.get(0).getStartRefPosition());
        assertEquals(92, pos1.get(0).getEndRefPosition());
        assertEquals(121, pos1.get(1).getStartRefPosition());
        assertEquals(132, pos1.get(1).getEndRefPosition());
       
        List<CitationPosition> pos2 = toSortedList(positions.get(2));
        assertEquals(1, pos2.size());
        assertEquals(245, pos2.get(0).getStartRefPosition());
        assertEquals(251, pos2.get(0).getEndRefPosition());
        
        List<CitationPosition> pos3 = toSortedList(positions.get(3));
        assertEquals(3, pos3.size());
        assertEquals(121, pos3.get(0).getStartRefPosition());
        assertEquals(132, pos3.get(0).getEndRefPosition());
        
        List<CitationPosition> pos4 = toSortedList(positions.get(4));
        assertEquals(1, pos4.size());
        assertEquals(245, pos4.get(0).getStartRefPosition());
        assertEquals(251, pos4.get(0).getEndRefPosition());
    }

    private List<CitationPosition> toSortedList(Set<CitationPosition> positions) {
        List<CitationPosition> list = new ArrayList<CitationPosition>(positions);
        Collections.sort(list, new Comparator<CitationPosition>() {
            @Override
            public int compare(CitationPosition o1, CitationPosition o2) {
                if (o1.getStartRefPosition() != o2.getStartRefPosition()) {
                    return Integer.compare(o1.getStartRefPosition(), o2.getStartRefPosition());
                }
                return Integer.compare(o1.getEndRefPosition(), o2.getEndRefPosition());
            }
        });
        return list;
    }
    
}
