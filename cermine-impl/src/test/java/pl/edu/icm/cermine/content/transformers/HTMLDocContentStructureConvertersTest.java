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
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class HTMLDocContentStructureConvertersTest {
    
    String modelFilePath = "/pl/edu/icm/cermine/content/model/model.xml";
    
    HTMLToDocContentStructReader reader;
    DocContentStructToHTMLWriter writer;
    
    @Before
    public void setUp() throws JDOMException, IOException, TransformationException, URISyntaxException {
        reader = new HTMLToDocContentStructReader();
        writer = new DocContentStructToHTMLWriter();
    }
    
 
    @Test
    public void readerWriterTest() throws URISyntaxException, IOException, TransformationException, SAXException {
        File file = new File(this.getClass().getResource(modelFilePath).toURI());
        String expectedHTML = FileUtils.readFileToString(file);
        
        InputStream is = this.getClass().getResourceAsStream(modelFilePath);
        InputStreamReader isr = new InputStreamReader(is);
        DocumentContentStructure structure = reader.read(isr);
        String structureHTML = writer.write(structure);
        
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expectedHTML, structureHTML);
        
        assertTrue(diff.similar());
    }
}
