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

package pl.edu.icm.cermine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.custommonkey.xmlunit.Diff;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class AltPdfNLMMetadataExtractorTest {
    
    private final static String ALT_METADATA_CLASSIFIER_MODEL_PATH = "classpath:/pl/edu/icm/cermine/structure/model-metadata-humanities";
    private final static String ALT_METADATA_CLASSIFIER_RANGE_PATH = "classpath:/pl/edu/icm/cermine/structure/model-metadata-humanities.range";
    
    static final private String TEST_FILE = "/pl/edu/icm/cermine/test3.pdf";
    static final private String EXP_FILE = "/pl/edu/icm/cermine/test3-metadata.xml";
    
    private ContentExtractor extractor;
    
    @Before
    public void setUp() throws AnalysisException, IOException {
        extractor = new ContentExtractor();
        
        extractor.getConf().setMetadataZoneClassifier(ComponentFactory.getMetadataZoneClassifier(ALT_METADATA_CLASSIFIER_MODEL_PATH, ALT_METADATA_CLASSIFIER_RANGE_PATH));
    }
    
    @Test
    public void metadataExtractionTest() throws AnalysisException, IOException, JDOMException, SAXException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_FILE);
        Element testMetadata;
        try {
            extractor.setPDF(testStream);
            testMetadata = extractor.getMetadataAsNLM();
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
        Element expMetadata = dom.getRootElement();
                
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Diff diff = new Diff(outputter.outputString(expMetadata), outputter.outputString(testMetadata));
        assertTrue(diff.similar());
    }
}
