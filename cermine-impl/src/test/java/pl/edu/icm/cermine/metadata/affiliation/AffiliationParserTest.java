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
package pl.edu.icm.cermine.metadata.affiliation;

import org.jdom.output.XMLOutputter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Bartosz Tarnawski
 */
public class AffiliationParserTest {

    @Test
    public void testEmpty() throws AnalysisException, TransformationException {
        CRFAffiliationParser parser = new CRFAffiliationParser();
        XMLOutputter outputter = new XMLOutputter();
        String input = "";
        String expected = "<aff id=\"\"><label></label></aff>";
        String actual = outputter.outputString(parser.parse(input));
        assertEquals(expected, actual);
    }
    
    @Test
    public void testTooLong() throws AnalysisException, TransformationException {
        CRFAffiliationParser parser = new CRFAffiliationParser();
        XMLOutputter outputter = new XMLOutputter();
        StringBuilder inputSB = new StringBuilder();
        for (int i = 0; i < 40; i++) {
            inputSB.append("Department of Oncology - Pathology, Karolinska Institutet, Stockholm, Sweden, ");
        }
        String input = inputSB.toString();
        String expected = "<aff id=\"\"><label></label>" + input.trim() + "</aff>";
        String actual = outputter.outputString(parser.parse(input));
        assertEquals(expected, actual);
    }

    @Test
    public void testOriental() throws AnalysisException, TransformationException {
        CRFAffiliationParser parser = new CRFAffiliationParser();
        XMLOutputter outputter = new XMLOutputter();
        String input = "山梨大学大学院医学工学総合研究部　社会医学講座";
        String expected = "<aff id=\"\"><label></label></aff>";
        String actual = outputter.outputString(parser.parse(input));
        assertEquals(expected, actual);
    }

    @Test
    public void testParseString() throws AnalysisException, TransformationException {
        CRFAffiliationParser parser = new CRFAffiliationParser();
        XMLOutputter outputter = new XMLOutputter();
        String input = "Department of Dinozauring, Dino Institute, Tyranosaurus Route 35, Boston, MA, USA";
        String expected = "<aff id=\"\"><label></label>"
                + "<institution>Department of Dinozauring, Dino Institute</institution>"
                + ", "
                + "<addr-line>Tyranosaurus Route 35, Boston, MA</addr-line>"
                + ", "
                + "<country country=\"US\">USA</country>"
                + "</aff>";
        String actual = outputter.outputString(parser.parse(input));
        assertEquals(expected, actual);
    }

    @Test
    public void testParseStringWithAuthor() throws AnalysisException, TransformationException {
        CRFAffiliationParser parser = new CRFAffiliationParser(
                "common-words-affiliations-with-author.txt",
                "acrf-affiliations-with-author.ser.gz");
        XMLOutputter outputter = new XMLOutputter();
        String input = "Andrew McDino and Elizabeth Pterodactyl, Department of Dinozauring, Dino Institute, Tyranosaurus Route 35, Boston, MA, USA";
        String expected = "<aff id=\"\"><label></label>"
                + //				"<author>Andrew McDino and Elizabeth Pterodactyl</author>" +
                ", "
                + "<institution>Department of Dinozauring, Dino Institute</institution>"
                + ", "
                + "<addr-line>Tyranosaurus Route 35, Boston, MA</addr-line>"
                + ", "
                + "<country country=\"US\">USA</country>"
                + "</aff>";
        String actual = outputter.outputString(parser.parse(input));
        assertEquals(expected, actual);
    }

}
