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

package pl.edu.icm.cermine.bibref.transformers;

import java.io.IOException;
import java.util.List;
import org.custommonkey.xmlunit.Diff;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.StandardDataExamples;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BibEntryToNLMElementConverterTest {
   
    private BibEntryToNLMConverter converter;
    
    private List<BibEntry> entries;
    private List<Element> elements;
    
    XMLOutputter xmlOutputter;

    @Before
    public void setUp() throws JDOMException, IOException {
        converter = new BibEntryToNLMConverter();
        entries = StandardDataExamples.getReferencesAsBibEntry();
        elements = StandardDataExamples.getReferencesAsNLMElement();
        xmlOutputter = new XMLOutputter();
    }
    
    @Test
    public void testConvert() throws TransformationException, SAXException, IOException {
        Element testElement = converter.convert(entries.get(0));
        Diff diff = new Diff(xmlOutputter.outputString(elements.get(0)), xmlOutputter.outputString(testElement));
        assertTrue(diff.similar());
    }
    
    @Test
    public void testConvertAll() throws TransformationException, SAXException, IOException {
        assertEquals(entries.size(), elements.size());
        List<Element> testElements = converter.convertAll(entries);
        for (int i = 0; i < elements.size(); i++) {
            Diff diff = new Diff(xmlOutputter.outputString(elements.get(i)), xmlOutputter.outputString(testElements.get(i)));
            assertTrue(diff.similar());
        }
    }
    
}