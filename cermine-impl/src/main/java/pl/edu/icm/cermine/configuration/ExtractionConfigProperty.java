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

/**
 * Enumeration that contains all available configuration properties for
 * {@link pl.edu.icm.cermine.ContentExtractor}
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public enum ExtractionConfigProperty {

    INITIAL_ZONE_CLASSIFIER_MODEL_PATH  ("zoneClassifier.initial.model"),
    INITIAL_ZONE_CLASSIFIER_RANGE_PATH  ("zoneClassifier.initial.ranges"),
    
    METADATA_ZONE_CLASSIFIER_MODEL_PATH ("zoneClassifier.metadata.model"),
    METADATA_ZONE_CLASSIFIER_RANGE_PATH ("zoneClassifier.metadata.ranges"),
    
    CONTENT_FILTER_MODEL_PATH           ("contentFilter.model"),
    CONTENT_FILTER_RANGE_PATH           ("contentFilter.ranges"),

    BIBREF_MODEL_PATH                   ("bibref.model"),
    BIBREF_TERMS_PATH                   ("bibref.terms"),
    
    IMAGES_EXTRACTION                   ("images.extraction"),
    
    DEBUG_PRINT_TIME                    ("debug.print.time");
    
    private final String propertyKey;

    private ExtractionConfigProperty(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

}
