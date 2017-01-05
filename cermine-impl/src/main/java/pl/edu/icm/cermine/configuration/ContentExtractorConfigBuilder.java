package pl.edu.icm.cermine.configuration;

import java.io.File;
import java.net.URL;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Loader of configuration properties for {@link pl.edu.icm.cermine.ContentExtractor}
 * 
 * @author madryk
 */
public class ContentExtractorConfigBuilder {

    private static final String DEFAULT_CONFIGURATION_CLASSPATH = "pl/edu/icm/cermine/application-default.properties";

    private final CompositeConfiguration configuration;

    public ContentExtractorConfigBuilder() {
        // prevents MALLET from printing info messages
        System.setProperty("java.util.logging.config.file",
            "edu/umass/cs/mallet/base/util/resources/logging.properties");
        this.configuration = new CompositeConfiguration();
    }
    
    public ContentExtractorConfigBuilder addConfiguration(String configurationFilePath) {
        try {
            configuration.addConfiguration(new PropertiesConfiguration(new File(configurationFilePath)));
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to load configuration from file " + configurationFilePath, e);
        }
        return this;
    }
    
    public ContentExtractorConfigBuilder addConfiguration(Configuration config) {
        configuration.addConfiguration(config);
        return this;
    }
    
    public ContentExtractorConfigBuilder setProperty(String property, Object value) {
        configuration.setProperty(property, value);
        return this;
    }

    public ContentExtractorConfig buildConfiguration() {
        URL propertiesUrl = ContentExtractorConfigBuilder.class.getClassLoader().getResource(DEFAULT_CONFIGURATION_CLASSPATH);
        try {
            configuration.addConfiguration(new PropertiesConfiguration(propertiesUrl));
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to load default configuration", e);
        }
        return new ContentExtractorConfig(configuration);
    }
    
}
