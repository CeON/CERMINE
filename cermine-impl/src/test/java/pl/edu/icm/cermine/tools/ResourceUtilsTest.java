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

package pl.edu.icm.cermine.tools;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;

/**
 * @author madryk
 */
public class ResourceUtilsTest {

    private final InputStream resourceStream = null;
    
    @After
    public void cleanup() throws IOException {
        if (resourceStream != null) {
            resourceStream.close();
        }
    }
    
    @Test
    public void openResourceStream_FROM_CLASSPATH() throws IOException {
        
        InputStream inputStream = ResourceUtils.openResourceStream("classpath:/pl/edu/icm/cermine/tools/resourceFile.txt");
        
        assertResourceContent(inputStream);
    }
    
    @Test
    public void openResourceStream_FROM_FILE() throws IOException {
        URL resourceUrl = ResourceUtilsTest.class.getClassLoader().getResource("pl/edu/icm/cermine/tools/resourceFile.txt");
        
        InputStream inputStream = ResourceUtils.openResourceStream(resourceUrl.getPath());
        
        assertResourceContent(inputStream);
    }
    
    private void assertResourceContent(InputStream inputStream) throws IOException {
        List<String> resourceLines = IOUtils.readLines(inputStream, Charset.forName("UTF-8"));
        assertEquals(1, resourceLines.size());
        assertEquals("Resource file content", resourceLines.get(0));
    }
}
