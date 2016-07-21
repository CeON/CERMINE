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

package pl.edu.icm.cermine.content.transformers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.jdom.JDOMException;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HTMLDocContentStructureConvertersTest {
    
    String modelFilePath = "/pl/edu/icm/cermine/content/model/model.xml";
    
    HTMLToDocContentReader reader;
    DocContentToHTMLWriter writer;
    
    @Before
    public void setUp() throws JDOMException, IOException, TransformationException, URISyntaxException {
        reader = new HTMLToDocContentReader();
        writer = new DocContentToHTMLWriter();
    }
    
 
    @Test
    public void readerWriterTest() throws URISyntaxException, IOException, TransformationException, SAXException {
        File file = new File(this.getClass().getResource(modelFilePath).toURI());
        String expectedHTML = FileUtils.readFileToString(file, "UTF-8");
        
        InputStream is = this.getClass().getResourceAsStream(modelFilePath);
        InputStreamReader isr = new InputStreamReader(is);
        ContentStructure structure = reader.read(isr);
        String structureHTML = writer.write(structure);
        
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expectedHTML, structureHTML);
        
        assertTrue(diff.similar());
    }
}
