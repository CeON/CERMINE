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
public class ContentExtractorConfigLoader {

    private static final String DEFAULT_CONFIGURATION_CLASSPATH = "pl/edu/icm/cermine/application-default.properties";

    private static final ThreadLocal<ContentExtractorConfig> INSTANCE =
            new ThreadLocal<ContentExtractorConfig>() {
        @Override
        protected ContentExtractorConfig initialValue() {
            return buildConfiguration();
        }
    };

    public static ContentExtractorConfig get() {
        return INSTANCE.get();
    }

    public static void remove() {
        INSTANCE.remove();
    }    
    
    /**
     * Returns {@link pl.edu.icm.cermine.ContentExtractor} configuration properties from property file.<br>
     * If some configuration property is not present in provided property file,
     * then the default value will be used.
     * @param configurationFilePath file path
     */
    public static void loadConfiguration(String configurationFilePath) {
        // prevents MALLET from printing info messages
        System.setProperty("java.util.logging.config.file",
            "edu/umass/cs/mallet/base/util/resources/logging.properties");
        try {
            INSTANCE.set(buildConfiguration(new PropertiesConfiguration(new File(configurationFilePath))));
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to load configuration from file " + configurationFilePath, e);
        }
    }

    private static ContentExtractorConfig buildConfiguration(Configuration... configs) {
        CompositeConfiguration configuration = new CompositeConfiguration();
        for (Configuration config : configs) {
            configuration.addConfiguration(config);
        }
        URL propertiesUrl = ContentExtractorConfigLoader.class.getClassLoader().getResource(DEFAULT_CONFIGURATION_CLASSPATH);
        try {
            configuration.addConfiguration(new PropertiesConfiguration(propertiesUrl));
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to load default configuration", e);
        }
        return new ContentExtractorConfig(configuration);
    }
    
}
