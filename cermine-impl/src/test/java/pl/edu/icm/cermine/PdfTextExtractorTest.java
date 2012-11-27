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
public class PdfTextExtractorTest {
    static final private String TEST_FILE = "/pl/edu/icm/cermine/test2.pdf";
    static final private String EXP_FILE = "/pl/edu/icm/cermine/test2.txt";
    
    private DocumentTextExtractor<String> extractor;
    
    @Before
    public void setUp() throws AnalysisException {
        extractor = new PdfTextExtractor();
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
