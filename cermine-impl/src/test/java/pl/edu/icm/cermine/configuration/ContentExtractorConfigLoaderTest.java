package pl.edu.icm.cermine.configuration;

import java.nio.charset.Charset;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pl.edu.icm.cermine.configuration.ContentExtractorConfig.ConfigurationProperty;

/**
 * @author madryk
 */
public class ContentExtractorConfigLoaderTest {

    @Test
    public void loadConfiguration_DEFAULT() {
        try {
            // execute        
            ContentExtractorConfig configuration = ContentExtractorConfigRegister.get();
        
            // assert
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-initial-default", configuration.getProperty(ConfigurationProperty.INITIAL_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-initial-default.range", configuration.getProperty(ConfigurationProperty.INITIAL_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-metadata-default", configuration.getProperty(ConfigurationProperty.METADATA_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-metadata-default.range", configuration.getProperty(ConfigurationProperty.METADATA_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals("classpath:/pl/edu/icm/cermine/content/filtering.model", configuration.getProperty(ConfigurationProperty.CONTENT_FILTER_MODEL_PATH));
            assertEquals("classpath:/pl/edu/icm/cermine/content/filtering.range", configuration.getProperty(ConfigurationProperty.CONTENT_FILTER_RANGE_PATH));
        } finally {
            ContentExtractorConfigRegister.remove();
        }
    }
    
    @Test
    public void loadConfiguration_OVERRIDE_DEFAULTS() {
        try {
            // given
            String configFilePath = ContentExtractorConfigLoaderTest.class.getClassLoader().getResource("pl/edu/icm/cermine/configuration/test-config.properties").getPath();
            System.out.println(Charset.defaultCharset());
            // execute
            ContentExtractorConfigRegister.set(new ContentExtractorConfigBuilder()
                    .addConfiguration(configFilePath)
                    .buildConfiguration()
            );
            ContentExtractorConfig configuration = ContentExtractorConfigRegister.get();
       
            // assert
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-initial-default", configuration.getProperty(ConfigurationProperty.INITIAL_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-initial-default.range", configuration.getProperty(ConfigurationProperty.INITIAL_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals("/path/to/metadata/classifier/model", configuration.getProperty(ConfigurationProperty.METADATA_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals("/path/to/metadata/classifier/range", configuration.getProperty(ConfigurationProperty.METADATA_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals("classpath:/pl/edu/icm/cermine/content/filtering.model", configuration.getProperty(ConfigurationProperty.CONTENT_FILTER_MODEL_PATH));
            assertEquals("classpath:/pl/edu/icm/cermine/content/filtering.range", configuration.getProperty(ConfigurationProperty.CONTENT_FILTER_RANGE_PATH));
        } finally {
            ContentExtractorConfigRegister.remove();
        }
    }
}
