package pl.edu.icm.cermine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

/**
 * @author madryk
 */
public class CommandLineOptionsParserTest {

    private CommandLineOptionsParser cmdLineOptionsParser = new CommandLineOptionsParser();
    
    
    @Test
    public void parse_NO_PATH() throws ParseException {
        // execute
        String error = cmdLineOptionsParser.parse(new String[] {});
        
        // assert
        assertEquals("\"path\" parameter not specified", error);
    }
    
    @Test(expected = ParseException.class)
    public void parse_NO_PATH_VALUE() throws ParseException {
        // execute
        cmdLineOptionsParser.parse(new String[] {"-path"});
    }
    
    @Test
    public void parse_WITH_PATH() throws ParseException {
        // execute
        
        String error = cmdLineOptionsParser.parse(new String[] {"-path", "/path/to/pdfs/folder"});
        
        // assert
        
        assertNull(error);
        assertEquals("/path/to/pdfs/folder", cmdLineOptionsParser.getPath());
        
        Map<String, String> typesAndExtensions = cmdLineOptionsParser.getTypesAndExtensions();
        assertEquals(1, typesAndExtensions.size());
        assertEquals("cermxml", typesAndExtensions.get("jats"));
        
        assertFalse(cmdLineOptionsParser.override());
        assertNull(cmdLineOptionsParser.getTimeout());
        assertEquals(3, cmdLineOptionsParser.getThreadsNumber());
        assertNull(cmdLineOptionsParser.getConfigurationPath());
        
        assertEquals("cermxml", cmdLineOptionsParser.getNLMExtension());
        assertEquals("cermtxt", cmdLineOptionsParser.getTextExtension());
        assertFalse(cmdLineOptionsParser.extractStructure());
        assertEquals("cxml", cmdLineOptionsParser.getBxExtension());
    }
    
    @Test
    public void parse_OVERRIDE_DEFAULTS() throws ParseException {
        // execute
        
        String error = cmdLineOptionsParser.parse(new String[] {
                "-path", "/path/to/pdfs/folder",
                "-outputs", "jats,zones,text",
                "-exts", "xml,data,txt",
                "-override",
                "-timeout", "120",
                "-configuration", "config.properties",
                "-threads", "8",
                
                "-ext", "xml2",
                "-str",
                "-strext", "xml3",});
        
        // assert
        
        assertNull(error);
        assertEquals("/path/to/pdfs/folder", cmdLineOptionsParser.getPath());
        
        Map<String, String> typesAndExtensions = cmdLineOptionsParser.getTypesAndExtensions();
        assertEquals(3, typesAndExtensions.size());
        assertEquals("xml", typesAndExtensions.get("jats"));
        assertEquals("data", typesAndExtensions.get("zones"));
        assertEquals("txt", typesAndExtensions.get("text"));
        
        assertTrue(cmdLineOptionsParser.override());
        assertEquals(Long.valueOf(120L), cmdLineOptionsParser.getTimeout());
        assertEquals(8, cmdLineOptionsParser.getThreadsNumber());
        assertEquals("config.properties", cmdLineOptionsParser.getConfigurationPath());
        
        assertEquals("xml2", cmdLineOptionsParser.getNLMExtension());
        assertEquals("xml2", cmdLineOptionsParser.getTextExtension());
        assertTrue(cmdLineOptionsParser.extractStructure());
        assertEquals("xml3", cmdLineOptionsParser.getBxExtension());
    }
    
    @Test
    public void parse_INVALID_EXTENSIONS_LIST_SIZE() throws ParseException {
        // execute
        
        String error = cmdLineOptionsParser.parse(new String[] {
                "-path", "/path/to/pdfs/folder",
                "-outputs", "jats,zones,trueviz",
                "-exts", "xml,xml2"});
        
        // assert
        
        assertEquals("\"output\" and \"exts\" lists have different lengths", error);
    }
    
    @Test
    public void parse_UNKNOWN_OUTPUT_TYPE() throws ParseException {
        // execute
        
        String error = cmdLineOptionsParser.parse(new String[] {
                "-path", "/path/to/pdfs/folder",
                "-outputs", "jats,unknown,unknown2",
                "-exts", "xml,xml2,xml3"});
        
        // assert
        
        assertEquals("Unknown output types: [unknown, unknown2]", error);
    }
}
