package pl.edu.icm.cermine.configuration;

import org.apache.commons.configuration.Configuration;

/**
 * Class that holds configuration for {@link pl.edu.icm.cermine.ContentExtractor}.<br>
 * An object of this class can be obtained by {@link pl.edu.icm.cermine.configuration.ContentExtractorConfigLoader}
 * 
 * @author madryk
 */
public class ContentExtractorConfig {

    /**
     * Enumeration that contains all available configuration properties for {@link pl.edu.icm.cermine.ContentExtractor}
     */
    public enum ConfigurationProperty {
        
        INITIAL_ZONE_CLASSIFIER_MODEL_PATH  ("zoneClassifier.initial.model"),
        INITIAL_ZONE_CLASSIFIER_RANGE_PATH  ("zoneClassifier.initial.ranges"),
        
        METADATA_ZONE_CLASSIFIER_MODEL_PATH ("zoneClassifier.metadata.model"),
        METADATA_ZONE_CLASSIFIER_RANGE_PATH ("zoneClassifier.metadata.ranges"),
        
        CONTENT_FILTER_MODEL_PATH           ("contentFilter.model"),
        CONTENT_FILTER_RANGE_PATH           ("contentFilter.ranges");
        
        private final String propertyKey;
        
        private ConfigurationProperty(String propertyKey) {
            this.propertyKey = propertyKey;
        }

        public String getPropertyKey() {
            return propertyKey;
        }
        
    }
    
    private final Configuration configuration;
    
    /**
     * Default constructor
     * 
     * @param configuration - configuration object that contains all the configuration properties
     *      available to {@link ContentExtractor}
     */
    ContentExtractorConfig(Configuration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * Returns value of the configuration property
     * @param property property
     * @return property value
     */
    public String getProperty(ConfigurationProperty property) {
        return configuration.getString(property.getPropertyKey());
    }

}
