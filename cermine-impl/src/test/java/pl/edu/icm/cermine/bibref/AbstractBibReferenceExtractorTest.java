package pl.edu.icm.cermine.bibref;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipFile;
import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 *
 * @author Dominika Tkaczyk
 */
public abstract class AbstractBibReferenceExtractorTest {
    static final private String TEST_FILE = "/pl/edu/icm/cermine/bibref/refs.xml.zip";
    static final private String EXP_FILE = "/pl/edu/icm/cermine/bibref/refs.txt";
    
    private TrueVizToBxDocumentReader bxReader;
    
    @Before
    public void setUp() {
        bxReader = new TrueVizToBxDocumentReader();
    }
    
    @Test
    public void metadataExtractionTest() throws AnalysisException, JDOMException, IOException, SAXException, TransformationException, URISyntaxException {
        InputStream expStream = this.getClass().getResourceAsStream(EXP_FILE);
        BufferedReader expReader = new BufferedReader(new InputStreamReader(expStream));
        
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = expReader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        expStream.close();
        expReader.close();
        
        URL url = this.getClass().getResource(TEST_FILE);
        ZipFile zipFile = new ZipFile(new File(url.toURI()));
        InputStream inputStream = zipFile.getInputStream(zipFile.getEntry("out.xml"));
        BxDocument expDocument = new BxDocument().setPages(bxReader.read(new InputStreamReader(inputStream)));
        String[] references = getExtractor().extractBibReferences(expDocument);
        
        assertEquals(StringUtils.join(references, "\n"), sb.toString().trim());
    }
    
    protected abstract BibReferenceExtractor getExtractor();
}
