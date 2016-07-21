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

package pl.edu.icm.cermine.content.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.jdom.JDOMException;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import pl.edu.icm.cermine.content.transformers.HTMLToDocContentReader;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.FormatToModelReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DocumentContentStructureTest {
    
    String modelFilePath = "/pl/edu/icm/cermine/content/model/model.xml";

    FormatToModelReader<ContentStructure> reader;
    ContentStructure structure;
    
    @Before
    public void setUp() throws JDOMException, IOException, TransformationException, URISyntaxException {
        reader = new HTMLToDocContentReader();
        
        InputStream is = this.getClass().getResourceAsStream(modelFilePath);
        InputStreamReader isr = new InputStreamReader(is);
        
        structure = reader.read(isr);
    }
    
    @Test
    public void structureTest() {
        assertEquals(4, structure.getSections().size());
        assertEquals("1. BACKGROUND", structure.getSections().get(0).getTitle());

        DocumentSection firstLevelStruct = structure.getSections().get(1);
                
        assertEquals(1, firstLevelStruct.getLevel());
        assertEquals("2. DATA MODELING AND MAPPING", firstLevelStruct.getTitle());
        
        assertEquals(3, firstLevelStruct.getSubsections().size());
        assertEquals("2.1 Lined Data", firstLevelStruct.getSubsections().get(0).getTitle());
    }

}
