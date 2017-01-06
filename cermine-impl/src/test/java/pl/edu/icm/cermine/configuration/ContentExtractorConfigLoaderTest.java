package pl.edu.icm.cermine.configuration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author madryk
 */
public class ContentExtractorConfigLoaderTest {

    @Test
    public void loadConfiguration_DEFAULT() {
        try {
            // execute        
            ExtractionConfig configuration = ExtractionConfigRegister.get();
        
            // assert
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-initial-default", configuration.getStringProperty(ExtractionConfigProperty.INITIAL_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-initial-default.range", configuration.getStringProperty(ExtractionConfigProperty.INITIAL_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-metadata-default", configuration.getStringProperty(ExtractionConfigProperty.METADATA_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-metadata-default.range", configuration.getStringProperty(ExtractionConfigProperty.METADATA_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals("classpath:/pl/edu/icm/cermine/content/filtering.model", configuration.getStringProperty(ExtractionConfigProperty.CONTENT_FILTER_MODEL_PATH));
            assertEquals("classpath:/pl/edu/icm/cermine/content/filtering.range", configuration.getStringProperty(ExtractionConfigProperty.CONTENT_FILTER_RANGE_PATH));
        } finally {
            ExtractionConfigRegister.remove();
        }
    }
    
    @Test
    public void loadConfiguration_OVERRIDE_DEFAULTS() {
        try {
            // given
            String configFilePath = ContentExtractorConfigLoaderTest.class.getClassLoader().getResource("pl/edu/icm/cermine/configuration/test-config.properties").getPath();

            // execute
            ExtractionConfigRegister.set(new ExtractionConfigBuilder()
                    .addConfiguration(configFilePath)
                    .buildConfiguration()
            );
            ExtractionConfig configuration = ExtractionConfigRegister.get();
       
            // assert
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-initial-default", configuration.getStringProperty(ExtractionConfigProperty.INITIAL_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals("classpath:/pl/edu/icm/cermine/structure/model-initial-default.range", configuration.getStringProperty(ExtractionConfigProperty.INITIAL_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals("/path/to/metadata/classifier/model", configuration.getStringProperty(ExtractionConfigProperty.METADATA_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals("/path/to/metadata/classifier/range", configuration.getStringProperty(ExtractionConfigProperty.METADATA_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals("classpath:/pl/edu/icm/cermine/content/filtering.model", configuration.getStringProperty(ExtractionConfigProperty.CONTENT_FILTER_MODEL_PATH));
            assertEquals("classpath:/pl/edu/icm/cermine/content/filtering.range", configuration.getStringProperty(ExtractionConfigProperty.CONTENT_FILTER_RANGE_PATH));
        } finally {
            ExtractionConfigRegister.remove();
        }
    }
}
