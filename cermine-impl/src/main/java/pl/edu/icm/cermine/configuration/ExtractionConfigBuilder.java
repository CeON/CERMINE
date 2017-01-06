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
public class ExtractionConfigBuilder {

    private static final String DEFAULT_CONFIGURATION_CLASSPATH = "pl/edu/icm/cermine/application-default.properties";

    private final CompositeConfiguration configuration;

    public ExtractionConfigBuilder() {
        // prevents MALLET from printing info messages
        System.setProperty("java.util.logging.config.file",
            "edu/umass/cs/mallet/base/util/resources/logging.properties");
        this.configuration = new CompositeConfiguration();
    }
    
    public ExtractionConfigBuilder addConfiguration(String configurationFilePath) {
        try {
            configuration.addConfiguration(new PropertiesConfiguration(new File(configurationFilePath)));
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to load configuration from file " + configurationFilePath, e);
        }
        return this;
    }
    
    public ExtractionConfigBuilder addConfiguration(Configuration config) {
        configuration.addConfiguration(config);
        return this;
    }
    
    public ExtractionConfigBuilder setProperty(ExtractionConfigProperty property, Object value) {
        configuration.setProperty(property.getPropertyKey(), value);
        return this;
    }

    public ExtractionConfig buildConfiguration() {
        URL propertiesUrl = ExtractionConfigBuilder.class.getClassLoader().getResource(DEFAULT_CONFIGURATION_CLASSPATH);
        try {
            configuration.addConfiguration(new PropertiesConfiguration(propertiesUrl));
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to load default configuration", e);
        }
        return new ExtractionConfig(configuration);
    }
    
}
