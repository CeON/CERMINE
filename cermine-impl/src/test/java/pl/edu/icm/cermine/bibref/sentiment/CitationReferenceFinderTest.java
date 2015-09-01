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

package pl.edu.icm.cermine.bibref.sentiment;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationContext;

/**
 *
 * @author Dominika Tkaczyk
 */
public class CitationReferenceFinderTest {
    
    private static String documentTextNoRefs = 
        "This is a state of the art fragment with absolutely no references whatsoever.";

    private static String documentTextSingleRefs = 
        "This is a typical state of the art fragment. We can reference a single document " +
        "like this [2] or [ 12].";

    private static String documentTextMultipleRefs = 
        "This is a typical state of the art fragment. Sometimes we use [3,2, 4, 12 ] to " +
        "reference multiple documents in one place.";

    private static String documentTextRanges = 
        "This is a typical state of the art fragment. To save space, the number can also " +
        "be given as ranges: [2-4] or [1-5, 7].";
    
    private static BibEntry[] citations = {
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
        CitationReferenceFinder finder = new CitationReferenceFinder();
        
        List<CitationContext> contexts = finder.findReferences(documentTextNoRefs, citations[2]);
        assertTrue(contexts.isEmpty());
    }
    
    @Test
    public void testReferenceFinderSingleRefs() {
        CitationReferenceFinder finder = new CitationReferenceFinder();
        
        List<CitationContext> contexts = finder.findReferences(documentTextSingleRefs, citations[0]);
        assertEquals(1, contexts.size());
        assertEquals(98, contexts.get(0).getStartRefPosition());
        assertEquals(101, contexts.get(0).getEndRefPosition());
        
        contexts = finder.findReferences(documentTextSingleRefs, citations[1]);
        assertEquals(1, contexts.size());
        assertEquals(91, contexts.get(0).getStartRefPosition());
        assertEquals(92, contexts.get(0).getEndRefPosition());
    }
    
    @Test
    public void testReferenceFinderMultipleRefs() {
        CitationReferenceFinder finder = new CitationReferenceFinder();
        
        List<CitationContext> contexts = finder.findReferences(documentTextMultipleRefs, citations[0]);
        assertEquals(1, contexts.size());
        assertEquals(63, contexts.get(0).getStartRefPosition());
        assertEquals(74, contexts.get(0).getEndRefPosition());
        
        contexts = finder.findReferences(documentTextMultipleRefs, citations[1]);
        assertEquals(1, contexts.size());
        assertEquals(63, contexts.get(0).getStartRefPosition());
        assertEquals(74, contexts.get(0).getEndRefPosition());
        
        contexts = finder.findReferences(documentTextMultipleRefs, citations[3]);
        assertEquals(1, contexts.size());
        assertEquals(63, contexts.get(0).getStartRefPosition());
        assertEquals(74, contexts.get(0).getEndRefPosition());
    }
    
    @Test
    public void testReferenceFinderRangeRefs() {
        CitationReferenceFinder finder = new CitationReferenceFinder();
        
        List<CitationContext> contexts = finder.findReferences(documentTextRanges, citations[1]);
        assertEquals(2, contexts.size());
        assertEquals(101, contexts.get(0).getStartRefPosition());
        assertEquals(104, contexts.get(0).getEndRefPosition());
        assertEquals(110, contexts.get(1).getStartRefPosition());
        assertEquals(116, contexts.get(1).getEndRefPosition());
        
        contexts = finder.findReferences(documentTextRanges, citations[3]);
        assertEquals(2, contexts.size());
        assertEquals(101, contexts.get(0).getStartRefPosition());
        assertEquals(104, contexts.get(0).getEndRefPosition());
        assertEquals(110, contexts.get(1).getStartRefPosition());
        assertEquals(116, contexts.get(1).getEndRefPosition());

        contexts = finder.findReferences(documentTextRanges, citations[4]);
        assertEquals(1, contexts.size());
        assertEquals(110, contexts.get(0).getStartRefPosition());
        assertEquals(116, contexts.get(0).getEndRefPosition());
    }
    
}
