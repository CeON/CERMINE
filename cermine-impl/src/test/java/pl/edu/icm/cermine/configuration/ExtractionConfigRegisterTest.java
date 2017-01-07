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

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author madryk
 */
public class ExtractionConfigRegisterTest {

    @Test
    public void loadConfiguration_DEFAULT() {
        try {
            // execute        
            ExtractionConfig configuration = ExtractionConfigRegister.get();
        
            // assert
            assertEquals(
                    "classpath:/pl/edu/icm/cermine/structure/model-initial-default", 
                    configuration.getStringProperty(ExtractionConfigProperty.INITIAL_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals(
                    "classpath:/pl/edu/icm/cermine/structure/model-initial-default.range", 
                    configuration.getStringProperty(ExtractionConfigProperty.INITIAL_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals(
                    "classpath:/pl/edu/icm/cermine/structure/model-metadata-default", 
                    configuration.getStringProperty(ExtractionConfigProperty.METADATA_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals(
                    "classpath:/pl/edu/icm/cermine/structure/model-metadata-default.range", 
                    configuration.getStringProperty(ExtractionConfigProperty.METADATA_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals(
                    "classpath:/pl/edu/icm/cermine/content/filtering.model", 
                    configuration.getStringProperty(ExtractionConfigProperty.CONTENT_FILTER_MODEL_PATH));
            assertEquals(
                    "classpath:/pl/edu/icm/cermine/content/filtering.range", 
                    configuration.getStringProperty(ExtractionConfigProperty.CONTENT_FILTER_RANGE_PATH));

            assertTrue(configuration.getBooleanProperty(ExtractionConfigProperty.IMAGES_EXTRACTION));
            
            assertFalse(configuration.getBooleanProperty(ExtractionConfigProperty.DEBUG_PRINT_TIME));
        } finally {
            ExtractionConfigRegister.remove();
        }
    }
    
    @Test
    public void loadConfiguration_OVERRIDE_DEFAULTS() {
        try {
            // given
            String configFilePath = ExtractionConfigRegisterTest.class.getClassLoader().getResource("pl/edu/icm/cermine/configuration/test-config.properties").getPath();
            
            // execute
            ExtractionConfigRegister.set(new ExtractionConfigBuilder()
                    .addConfiguration(configFilePath)
                    .setProperty(ExtractionConfigProperty.IMAGES_EXTRACTION, false)
                    .buildConfiguration()
            );
            ExtractionConfig configuration = ExtractionConfigRegister.get();
       
            // assert
            assertEquals(
                    "classpath:/pl/edu/icm/cermine/structure/model-initial-default", 
                    configuration.getStringProperty(ExtractionConfigProperty.INITIAL_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals(
                    "classpath:/pl/edu/icm/cermine/structure/model-initial-default.range", 
                    configuration.getStringProperty(ExtractionConfigProperty.INITIAL_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals(
                    "/path/to/metadata/classifier/model", 
                    configuration.getStringProperty(ExtractionConfigProperty.METADATA_ZONE_CLASSIFIER_MODEL_PATH));
            assertEquals(
                    "/path/to/metadata/classifier/range", 
                    configuration.getStringProperty(ExtractionConfigProperty.METADATA_ZONE_CLASSIFIER_RANGE_PATH));
        
            assertEquals(
                    "classpath:/pl/edu/icm/cermine/content/filtering.model", 
                    configuration.getStringProperty(ExtractionConfigProperty.CONTENT_FILTER_MODEL_PATH));
            assertEquals(
                    "classpath:/pl/edu/icm/cermine/content/filtering.range", 
                    configuration.getStringProperty(ExtractionConfigProperty.CONTENT_FILTER_RANGE_PATH));
            
            assertFalse(configuration.getBooleanProperty(ExtractionConfigProperty.IMAGES_EXTRACTION));
                        
            assertTrue(configuration.getBooleanProperty(ExtractionConfigProperty.DEBUG_PRINT_TIME));
        } finally {
            ExtractionConfigRegister.remove();
        }
    }
}
