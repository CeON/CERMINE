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

    private final CommandLineOptionsParser cmdLineOptionsParser = new CommandLineOptionsParser();
      
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
        assertEquals(2, typesAndExtensions.size());
        assertEquals("cermxml", typesAndExtensions.get("jats"));
        assertEquals("images", typesAndExtensions.get("images"));
        
        assertFalse(cmdLineOptionsParser.override());
        assertNull(cmdLineOptionsParser.getTimeout());
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
