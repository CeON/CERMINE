package pl.edu.icm.cermine.content.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.jdom.JDOMException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import pl.edu.icm.cermine.content.transformers.HTMLToDocContentStructReader;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.FormatToModelReader;

/**
 *
 * @author Dominika Tkaczyk
 */
public class DocumentContentStructureTest {
    
    String modelFilePath = "/pl/edu/icm/cermine/content/model/model.xml";

    FormatToModelReader<DocumentContentStructure> reader;
    DocumentContentStructure structure;
    
    @Before
    public void setUp() throws JDOMException, IOException, TransformationException, URISyntaxException {
        reader = new HTMLToDocContentStructReader();
        
        InputStream is = this.getClass().getResourceAsStream(modelFilePath);
        InputStreamReader isr = new InputStreamReader(is);
        
        structure = reader.read(isr);
    }
    
    @Test
    public void topLevelStructureTest() {
        assertNull(structure.getHeader());
        assertNull(structure.getParent());
        assertEquals(4, structure.getParts().size());
        
        assertEquals(11, structure.getAllParagraphs().size());
        assertEquals(11, structure.getAllParagraphCount());
        assertEquals(11, structure.getAllParagraphTexts().size());
       
        assertEquals(10, structure.getHeaders().size());
        assertEquals(10, structure.getAllHeaderTexts().size());
        assertEquals("1. BACKGROUND", structure.getAllHeaderTexts().get(0));
        assertEquals(10, structure.getAllHeaderCount());
        
        assertTrue(structure.containsHeaderText("3.1 OAI-PMH Data Provider"));
        assertTrue(structure.containsHeaderFirstLineText("3.1 OAI-PMH Data Provider"));
        assertFalse(structure.containsHeaderText("false"));
        assertFalse(structure.containsHeaderFirstLineText("false"));
    }
    
    @Test
    public void firstLevelStructureTest() {
        DocumentContentStructure firstLevelStruct = structure.getParts().get(1);
        assertNotNull(firstLevelStruct.getHeader());
        
        assertEquals(1, firstLevelStruct.getHeader().getLevel());
        assertEquals("2. DATA MODELING AND MAPPING", firstLevelStruct.getHeader().getText());
        assertEquals(firstLevelStruct, firstLevelStruct.getHeader().getContentStructure());
        assertEquals(firstLevelStruct.getParent(), structure);
        
        assertEquals(3, firstLevelStruct.getParts().size());
        assertEquals(3, firstLevelStruct.getAllParagraphs().size());
        assertEquals(3, firstLevelStruct.getAllParagraphCount());
        assertEquals(3, firstLevelStruct.getAllParagraphTexts().size());
        
        assertEquals(4, firstLevelStruct.getHeaders().size());
        assertEquals(4, firstLevelStruct.getAllHeaderTexts().size());
        assertEquals("2.1 Lined Data", firstLevelStruct.getAllHeaderTexts().get(1));
        assertEquals(4, firstLevelStruct.getAllHeaderCount());
        
        assertTrue(firstLevelStruct.containsHeaderText("2.3 Dublin Core"));
        assertTrue(firstLevelStruct.containsHeaderFirstLineText("2.3 Dublin Core"));
        
        assertFalse(firstLevelStruct.containsHeaderText("test"));
        assertFalse(firstLevelStruct.containsHeaderFirstLineText("test"));
        
        assertNotNull(structure.getPrevHeader(firstLevelStruct.getHeader()));
        assertEquals("1. BACKGROUND", structure.getPrevHeader(firstLevelStruct.getHeader()).getText());
    }

}
