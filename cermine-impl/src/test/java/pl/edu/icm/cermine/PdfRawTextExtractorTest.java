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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.jdom.JDOMException;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfRawTextExtractorTest {
    static final private String TEST_FILE = "/pl/edu/icm/cermine/test2.pdf";
    static final private String EXP_FILE = "/pl/edu/icm/cermine/test2.txt";
    
    private DocumentTextExtractor<String> extractor;
    
    @Before
    public void setUp() throws AnalysisException {
        extractor = new PdfRawTextExtractor();
    }
    
    @Test
    public void textExtractionTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_FILE);
        String testContent;
        try {
            testContent = extractor.extractText(testStream);
        } finally {
            testStream.close();
        }
        
        InputStream expStream = this.getClass().getResourceAsStream(EXP_FILE);
        InputStreamReader expReader = new InputStreamReader(expStream);
        BufferedReader reader = new BufferedReader(expReader);
        
        String line;
        StringBuilder expectedContent = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                expectedContent.append(line);
                expectedContent.append("\n");
            }
        } finally {
            expStream.close();
            expReader.close();
            reader.close();
        }
        
        assertEquals(testContent.trim(), expectedContent.toString().trim());
    }
}
