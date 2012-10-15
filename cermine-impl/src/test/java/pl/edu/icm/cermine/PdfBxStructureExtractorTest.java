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
    public void setUp() throws AnalysisException {
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
