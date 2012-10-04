package pl.edu.icm.coansys.metaextr;

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
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfMetadataExtractorTest {
    static final private String TEST_FILE = "/pl/edu/icm/coansys/metaextr/test.pdf";
    static final private String EXP_FILE = "/pl/edu/icm/coansys/metaextr/metadata.xml";
    
    private DocumentMetadataExtractor<Element> extractor;
    
    @Before
    public void setUp() throws IOException {
        extractor = new PdfMetadataExtractor();
    }
    
    @Test
    public void metadataExtractionTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_FILE);
        Element testMetadata = extractor.extractMetadata(testStream);
        
        InputStream expStream = this.getClass().getResourceAsStream(EXP_FILE);
        InputStreamReader expReader = new InputStreamReader(expStream);
        SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        Document dom = saxBuilder.build(expReader);
        Element expMetadata = dom.getRootElement();
        
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Diff diff = new Diff(outputter.outputString(expMetadata), outputter.outputString(testMetadata));
        assertTrue(diff.similar());
    }
}
