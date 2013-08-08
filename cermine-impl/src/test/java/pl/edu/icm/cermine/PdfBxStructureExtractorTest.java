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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipFile;
import org.jdom.JDOMException;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfBxStructureExtractorTest {
    static final private String TEST_FILE = "/pl/edu/icm/cermine/test1.pdf";
    static final private String EXP_FILE = "test1-str.xml";
    static final private String EXP_ZIP_FILE = "/pl/edu/icm/cermine/test1-str.xml.zip";
    
    private DocumentStructureExtractor extractor;
    private TrueVizToBxDocumentReader reader;
    
    @Before
    public void setUp() throws AnalysisException, IOException {
        extractor = new PdfBxStructureExtractor();
        reader = new TrueVizToBxDocumentReader();
    }
    
    @Test
    public void metadataExtractionTest() throws AnalysisException, JDOMException, IOException, SAXException, TransformationException, URISyntaxException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_FILE);
        BxDocument testDocument;
        try {
            testDocument = extractor.extractStructure(testStream);
        } finally {
            testStream.close();
        }
        
        URL url = this.getClass().getResource(EXP_ZIP_FILE);
        ZipFile zipFile = new ZipFile(new File(url.toURI()));
        InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(EXP_FILE));
        BxDocument expDocument = new BxDocument().setPages(reader.read(new InputStreamReader(inputStream)));
        
        assertTrue(BxModelUtils.areEqual(expDocument, testDocument));
    }
}
