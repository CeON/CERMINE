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

    private InputStream resourceStream = null;
    
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
