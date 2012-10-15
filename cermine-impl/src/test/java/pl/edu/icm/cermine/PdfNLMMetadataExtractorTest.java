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
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMMetadataExtractorTest {
    static final private String TEST_FILE = "/pl/edu/icm/cermine/test1.pdf";
    static final private String EXP_FILE = "/pl/edu/icm/cermine/test1-met.xml";
    
    private DocumentMetadataExtractor<Element> extractor;
    
    @Before
    public void setUp() throws AnalysisException {
        extractor = new PdfNLMMetadataExtractor();
    }
    
    @Test
    public void metadataExtractionTest() throws AnalysisException, IOException, JDOMException, SAXException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_FILE);
        Element testMetadata;
        try {
            testMetadata = extractor.extractMetadata(testStream);
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
