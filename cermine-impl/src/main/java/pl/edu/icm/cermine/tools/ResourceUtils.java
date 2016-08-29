package pl.edu.icm.cermine.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;

public class ResourceUtils {

    private static final String CLASSPATH_RESOURCE_PREFIX = "classpath:";
    
    public static InputStream openResourceStream(String resourcePath) throws IOException {

        if (StringUtils.startsWith(resourcePath, CLASSPATH_RESOURCE_PREFIX)) {
            InputStream inputStream = ResourceUtils.class.getResourceAsStream(resourcePath.substring(CLASSPATH_RESOURCE_PREFIX.length()));
            
            return inputStream;
        }
        return new FileInputStream(resourcePath);
    }
}
