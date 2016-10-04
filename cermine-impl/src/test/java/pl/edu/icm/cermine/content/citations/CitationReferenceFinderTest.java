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

import pl.edu.icm.cermine.content.citations.CitationPositionFinder;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.content.citations.CitationPosition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
            .addField(BibEntry.FIELD_AUTHOR, "Hoeffding, W.")
            .addField(BibEntry.FIELD_TITLE, "Probability inequalities for sums of bounded random variables")
            .addField(BibEntry.FIELD_JOURNAL, "J. Amer. Statist. Assoc")
            .addField(BibEntry.FIELD_VOLUME, "58")
            .addField(BibEntry.FIELD_YEAR, "1963")
            .addField(BibEntry.FIELD_PAGES, "13--30"),
        new BibEntry().setText("[2]  Agranovitch (M.S.) and  Vishisk (M.I.). — Elliptic problems with a parameter and parabolic problems of general type, Russian Math. Surveys, 19, 1964, 53-157.")
            .addField(BibEntry.FIELD_AUTHOR, "Agranovitch, M.S.")
            .addField(BibEntry.FIELD_AUTHOR, "Vishisk, M.I.")
            .addField(BibEntry.FIELD_TITLE, "Elliptic problems with a parameter and parabolic problems of general type")
            .addField(BibEntry.FIELD_JOURNAL, "Russian Math. Surveys")
            .addField(BibEntry.FIELD_VOLUME, "19")
            .addField(BibEntry.FIELD_YEAR, "1964")
            .addField(BibEntry.FIELD_PAGES, "53--157"),
        new BibEntry().setText("5.  M-Y. Wang,  X. Wang and  D. Guo, A level-set method for structural topology optimization. Comput. Methods Appl. Mech. Engrg, 192 (2003) 227–246.")
            .addField(BibEntry.FIELD_AUTHOR, "Wang, M-Y.")
            .addField(BibEntry.FIELD_AUTHOR, "Wang, X.")
            .addField(BibEntry.FIELD_AUTHOR, "Guo, D.")
            .addField(BibEntry.FIELD_TITLE, "A level-set method for structural topology optimization")
            .addField(BibEntry.FIELD_JOURNAL, "Comput. Methods Appl. Mech. Engrg")
            .addField(BibEntry.FIELD_VOLUME, "192")
            .addField(BibEntry.FIELD_YEAR, "2003")
            .addField(BibEntry.FIELD_PAGES, "227--246"),
        new BibEntry().setText("  [4] R. Kobayashi, Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities, Math. Ann. 272 (1985), 385-398.")
            .addField(BibEntry.FIELD_AUTHOR, "Kobayashi, R.")
            .addField(BibEntry.FIELD_TITLE, "Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities")
            .addField(BibEntry.FIELD_JOURNAL, "Math. Ann.")
            .addField(BibEntry.FIELD_VOLUME, "272")
            .addField(BibEntry.FIELD_YEAR, "1985")
            .addField(BibEntry.FIELD_PAGES, "385--398"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .setText("7. W. C. Lee, Y. E. Chavez, T. Baker, and B. R. Luce, “Economic burden of heart failure: a summary of recent literature,” Heart and Lung, vol. 33, no. 6, pp. 362–371, 2004.")
            .addField(BibEntry.FIELD_AUTHOR, "Lee, W. C.")
            .addField(BibEntry.FIELD_AUTHOR, "Chavez, Y. E.")
            .addField(BibEntry.FIELD_AUTHOR, "Baker, T.")
            .addField(BibEntry.FIELD_AUTHOR, "Luce, B. R.")
            .addField(BibEntry.FIELD_TITLE, "Economic burden of heart failure: a summary of recent literature")
            .addField(BibEntry.FIELD_JOURNAL, "Heart and Lung")
            .addField(BibEntry.FIELD_VOLUME, "33")
            .addField(BibEntry.FIELD_NUMBER, "6")
            .addField(BibEntry.FIELD_YEAR, "2004")
            .addField(BibEntry.FIELD_PAGES, "362--371"),
        
    };

    @Test
    public void testReferenceFinderNoRefs() {
        CitationPositionFinder finder = new CitationPositionFinder();
        
        List<List<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT1, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());
        for (List<CitationPosition> pos : positions) {
            assertTrue(pos.isEmpty());
        }
    }

    @Test
    public void testReferenceFinderSingleRefs() {
        CitationPositionFinder finder = new CitationPositionFinder();
        
        List<List<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT2, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());

        assertEquals(1, positions.get(0).size());
        assertEquals(98, positions.get(0).get(0).getStartRefPosition());
        assertEquals(101, positions.get(0).get(0).getEndRefPosition());
        
        assertEquals(1, positions.get(1).size());
        assertEquals(91, positions.get(1).get(0).getStartRefPosition());
        assertEquals(92, positions.get(1).get(0).getEndRefPosition());
    }
    
    @Test
    public void testReferenceFinderMultipleRefs() {
        CitationPositionFinder finder = new CitationPositionFinder();
        
        List<List<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT3, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());
        
        assertEquals(1, positions.get(0).size());
        assertEquals(63, positions.get(0).get(0).getStartRefPosition());
        assertEquals(74, positions.get(0).get(0).getEndRefPosition());
        
        assertEquals(1, positions.get(1).size());
        assertEquals(63, positions.get(1).get(0).getStartRefPosition());
        assertEquals(74, positions.get(1).get(0).getEndRefPosition());
        
        assertEquals(1, positions.get(3).size());
        assertEquals(63, positions.get(3).get(0).getStartRefPosition());
        assertEquals(74, positions.get(3).get(0).getEndRefPosition());
    }
    
    @Test
    public void testReferenceFinderRangeRefs() {
        CitationPositionFinder finder = new CitationPositionFinder();
        
        List<List<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT4, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());
        
        assertEquals(2, positions.get(1).size());
        assertEquals(101, positions.get(1).get(0).getStartRefPosition());
        assertEquals(104, positions.get(1).get(0).getEndRefPosition());
        assertEquals(110, positions.get(1).get(1).getStartRefPosition());
        assertEquals(116, positions.get(1).get(1).getEndRefPosition());

        assertEquals(2, positions.get(3).size());
        assertEquals(101, positions.get(3).get(0).getStartRefPosition());
        assertEquals(104, positions.get(3).get(0).getEndRefPosition());
        assertEquals(110, positions.get(3).get(1).getStartRefPosition());
        assertEquals(116, positions.get(3).get(1).getEndRefPosition());
        
        assertEquals(1, positions.get(4).size());
        assertEquals(110, positions.get(4).get(0).getStartRefPosition());
        assertEquals(116, positions.get(4).get(0).getEndRefPosition());
    }
    
    @Test
    public void testReferenceFinderAuthorNames() {
        CitationPositionFinder finder = new CitationPositionFinder();
    
        List<List<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT5, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());
        
        assertEquals(1, positions.get(0).size());
        assertEquals(88, positions.get(0).get(0).getStartRefPosition());
        assertEquals(105, positions.get(0).get(0).getEndRefPosition());
     
        assertEquals(2, positions.get(1).size());
        assertEquals(122, positions.get(1).get(0).getStartRefPosition());
        assertEquals(153, positions.get(1).get(0).getEndRefPosition());
        assertEquals(181, positions.get(1).get(1).getStartRefPosition());
        assertEquals(249, positions.get(1).get(1).getEndRefPosition());
       
        assertEquals(1, positions.get(2).size());
        assertEquals(181, positions.get(2).get(0).getStartRefPosition());
        assertEquals(249, positions.get(2).get(0).getEndRefPosition());
        
        assertEquals(0, positions.get(3).size());
        
        assertEquals(1, positions.get(4).size());
        assertEquals(181, positions.get(4).get(0).getStartRefPosition());
        assertEquals(249, positions.get(4).get(0).getEndRefPosition());
    }
    
    @Test
    public void testReferenceFinderRoundBrackets() {
        CitationPositionFinder finder = new CitationPositionFinder();
    
        List<List<CitationPosition>> positions = finder.findReferences(DOCUMENT_TEXT6, Arrays.asList(CITATIONS));
        assertEquals(5, positions.size());
        
        assertEquals(1, positions.get(0).size());
        assertEquals(121, positions.get(0).get(0).getStartRefPosition());
        assertEquals(132, positions.get(0).get(0).getEndRefPosition());
     
        assertEquals(4, positions.get(1).size());
        assertEquals(91, positions.get(1).get(0).getStartRefPosition());
        assertEquals(92, positions.get(1).get(0).getEndRefPosition());
        assertEquals(121, positions.get(1).get(1).getStartRefPosition());
        assertEquals(132, positions.get(1).get(1).getEndRefPosition());
       
        assertEquals(1, positions.get(2).size());
        assertEquals(245, positions.get(2).get(0).getStartRefPosition());
        assertEquals(251, positions.get(2).get(0).getEndRefPosition());
        
        assertEquals(3, positions.get(3).size());
        assertEquals(121, positions.get(3).get(0).getStartRefPosition());
        assertEquals(132, positions.get(3).get(0).getEndRefPosition());
        
        assertEquals(1, positions.get(4).size());
        assertEquals(245, positions.get(4).get(0).getStartRefPosition());
        assertEquals(251, positions.get(4).get(0).getEndRefPosition());
    }

}
