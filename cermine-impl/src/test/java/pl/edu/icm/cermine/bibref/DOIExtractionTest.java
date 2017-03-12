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

package pl.edu.icm.cermine.bibref;

import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import pl.edu.icm.cermine.bibref.model.BibEntryFieldType;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DOIExtractionTest {
    
    private final CRFBibReferenceParser parser;

    public DOIExtractionTest() throws AnalysisException {
        this.parser = CRFBibReferenceParser.getInstance();
    }

    @Test
    public void testDOIRegular() throws AnalysisException {
        String input = "Lu TK, Collins JJ. 2007 Dispersing biofilms. Proc. Natl Acad. Sci. USA 104, 11 197–11 202. doi:10.1073/pnas.0704624104";
        BibEntry actual = parser.parseBibReference(input);
        assertEquals(input, actual.getText());
        assertEquals("10.1073/pnas.0704624104", actual.getFirstFieldValue(BibEntryFieldType.DOI));
    }
    
    @Test
    public void testDOIWithSuffix() throws AnalysisException {
        String input = "Lu TK, Collins JJ. 2007 Dispersing biofilms. Proc. Natl Acad. Sci. USA 104, 11 197–11 202. doi:10.1073/pnas.0704624104 http://address";
        BibEntry actual = parser.parseBibReference(input);
        assertEquals(input, actual.getText());
        assertEquals("10.1073/pnas.0704624104", actual.getFirstFieldValue(BibEntryFieldType.DOI));
    }
    
    @Test
    public void testDOIInParentheses() throws AnalysisException {
        String input = "Lu TK, Collins JJ. 2007 Dispersing biofilms. Proc. Natl Acad. Sci. USA 104, 11 197–11 202. (doi:10.1073/pnas.0704624104)";
        BibEntry actual = parser.parseBibReference(input);
        assertEquals(input, actual.getText());
        assertEquals("10.1073/pnas.0704624104", actual.getFirstFieldValue(BibEntryFieldType.DOI));
    }
    
    @Test
    public void testDOIInSquareBrackets() throws AnalysisException {
        String input = "Lu TK, Collins JJ. 2007 Dispersing biofilms. Proc. Natl Acad. Sci. USA 104, 11 197–11 202. [doi:10.1073/pnas.0704624104]";
        BibEntry actual = parser.parseBibReference(input);
        assertEquals(input, actual.getText());
        assertEquals("10.1073/pnas.0704624104", actual.getFirstFieldValue(BibEntryFieldType.DOI));
    }
    
    @Test
    public void testDOIMoreDigits() throws AnalysisException {
        String input = "Lu TK, Collins JJ. 2007 Dispersing biofilms. Proc. Natl Acad. Sci. USA 104, 11 197–11 202. 10.1073309/pnas.0704624104";
        BibEntry actual = parser.parseBibReference(input);
        assertEquals(input, actual.getText());
        assertEquals("10.1073309/pnas.0704624104", actual.getFirstFieldValue(BibEntryFieldType.DOI));
    }
}
