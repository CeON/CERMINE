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

package pl.edu.icm.cermine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.custommonkey.xmlunit.Diff;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMReferencesExtractorTest {
    static final private String TEST_FILE = "/pl/edu/icm/cermine/test2.pdf";
    static final private String EXP_FILE = "/pl/edu/icm/cermine/test2-cont.xml";
    
    private PdfNLMReferencesExtractor extractor;
    
    @Before
    public void setUp() throws AnalysisException, IOException {
        extractor = new PdfNLMReferencesExtractor();
    }
    
    @Test
    public void metadataExtractionTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_FILE);
        Element[] testReferences;
        try {
            testReferences = extractor.extractReferencesAsNLM(testStream);
        } finally {
            testStream.close();
        }
        
        InputStream expStream = this.getClass().getResourceAsStream(EXP_FILE);
        InputStreamReader expReader = new InputStreamReader(expStream);
        SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        Document dom;
        try {
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            expReader.close();
        }
        Element expNLM = dom.getRootElement();
        Element back = expNLM.getChild("back");
        Element refList = back.getChild("ref-list");
        
        List<Element> expReferences = new ArrayList<Element>();
        for (Object ref : refList.getChildren("ref")) {
            if (ref instanceof Element) {
                Element mixedCitation = ((Element)ref).getChild("mixed-citation");
                expReferences.add(mixedCitation);
            }
        }
        
        assertEquals(testReferences.length, expReferences.size());
        
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        for (int i = 0; i < testReferences.length; i++) {
            Diff diff = new Diff(outputter.outputString(testReferences[i]), outputter.outputString(expReferences.get(i)));
            assertTrue(diff.similar());
        }
    }
}
