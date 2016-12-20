package pl.edu.icm.cermine.configuration;

import java.io.File;
import java.net.URL;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Loader of configuration properties for {@link pl.edu.icm.cermine.ContentExtractor}
 * 
 * @author madryk
 */
public class ContentExtractorConfigLoader {

    private static final String DEFAULT_CONFIGURATION_CLASSPATH = "pl/edu/icm/cermine/application-default.properties";
    
    
    /**
     * Returns {@link pl.edu.icm.cermine.ContentExtractor} default configuration properties.
     * 
     * @return ContentExtractorConfig
     * @see "classpath:/pl/edu/icm/cermine/application-default.properties"
     */
    public ContentExtractorConfig loadConfiguration() {
        // prevents MALLET from printing info messages
        System.setProperty("java.util.logging.config.file",
            "edu/umass/cs/mallet/base/util/resources/logging.properties");
        
        CompositeConfiguration configuration = new CompositeConfiguration();
        
        addDefaultConfiguration(configuration);
        
        return new ContentExtractorConfig(configuration);
    }
    
    /**
     * Returns {@link pl.edu.icm.cermine.ContentExtractor} configuration properties from property file.<br>
     * If some configuration property is not present in provided property file,
     * then the default value will be used.
     * @param configurationFilePath file path
     * @return ContentExtractorConfig
     */
    public ContentExtractorConfig loadConfiguration(String configurationFilePath) {
        // prevents MALLET from printing info messages
        System.setProperty("java.util.logging.config.file",
            "edu/umass/cs/mallet/base/util/resources/logging.properties");
        
        CompositeConfiguration configuration = new CompositeConfiguration();
        
        try {
            configuration.addConfiguration(new PropertiesConfiguration(new File(configurationFilePath)));
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to load configuration from file " + configurationFilePath, e);
        }
        
        addDefaultConfiguration(configuration);
        
        return new ContentExtractorConfig(configuration);
    }
    
    
    private void addDefaultConfiguration(CompositeConfiguration configuration) {
        
        URL propertiesUrl = ContentExtractorConfigLoader.class.getClassLoader().getResource(DEFAULT_CONFIGURATION_CLASSPATH);
        
        try {
            configuration.addConfiguration(new PropertiesConfiguration(propertiesUrl));
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to load default configuration", e);
        }
    }
    
}
