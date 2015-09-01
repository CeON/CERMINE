/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.bibref.sentiment.model;

import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author Dominika Tkaczyk
 */
public class CitationSentiment {

    private String key;
    
    private Set<CiTOProperty> properties;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Set<CiTOProperty> getProperties() {
        if (properties == null) {
            properties = EnumSet.noneOf(CiTOProperty.class);
        }
        return properties;
    }

    public void setProperties(Set<CiTOProperty> properties) {
        this.properties = properties;
    }
    
    public void addProperty(CiTOProperty property) {
        if (properties == null) {
            properties = EnumSet.noneOf(CiTOProperty.class);
        }
        if (property != null) {
            properties.add(property);
        }
    }
    
}
