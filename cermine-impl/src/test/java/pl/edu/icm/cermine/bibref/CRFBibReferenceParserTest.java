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

import org.junit.Before;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CRFBibReferenceParserTest extends AbstractBibReferenceParserTest {
    
    private final double minPercentage = 0.8;
    
    private CRFBibReferenceParser parser;
    
    @Before
    public void setUp() throws AnalysisException {
        parser = CRFBibReferenceParser.getInstance();
    }

    @Override
    protected BibReferenceParser<BibEntry> getParser() {
        return parser;
    }

    @Override
    protected double getMinPercentage() {
        return minPercentage;
    }
    
    @Test
    public void testTooLong() throws AnalysisException {
        StringBuilder inputSB = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            inputSB.append("Johnson J, Dutton S, Briffa E, ");
        }
        inputSB.append("Broadband learning for doctors. BMJ 2006, 332, 1403-1404.");
        String input = inputSB.toString();
        BibEntry actual = parser.parseBibReference(input);
        assertEquals(input, actual.getText());
        assertTrue(actual.getFieldKeys().isEmpty());
    }
    
}
