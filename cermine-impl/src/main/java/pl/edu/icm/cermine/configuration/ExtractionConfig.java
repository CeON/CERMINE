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

import org.apache.commons.configuration.Configuration;

/**
 * Class that holds configuration for {@link pl.edu.icm.cermine.ContentExtractor}.<br>
 * An object of this class can be obtained by {@link pl.edu.icm.cermine.configuration.ExtractionConfigRegister}
 * 
 * @author madryk
 */
public class ExtractionConfig {
    
    private final Configuration configuration;
    
    /**
     * Default constructor
     * 
     * @param configuration - configuration object that contains all the configuration properties
     *      available to {@link ContentExtractor}
     */
    ExtractionConfig(Configuration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * Returns value of the configuration property
     * @param property property
     * @return property value
     */
    public String getStringProperty(ExtractionConfigProperty property) {
        return configuration.getString(property.getPropertyKey());
    }

    public boolean getBooleanProperty(ExtractionConfigProperty property) {
        return configuration.getBoolean(property.getPropertyKey());
    }
}
