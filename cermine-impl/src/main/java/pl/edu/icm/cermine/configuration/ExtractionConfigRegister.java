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

import com.google.common.base.Preconditions;

/**
 * Loader of configuration properties for {@link pl.edu.icm.cermine.ContentExtractor}
 * 
 * @author madryk
 */
public class ExtractionConfigRegister {

    private static final ThreadLocal<ExtractionConfig> INSTANCE =
            new ThreadLocal<ExtractionConfig>() {
        @Override
        protected ExtractionConfig initialValue() {
            return new ExtractionConfigBuilder().buildConfiguration();
        }
    };
    
    public static void set(ExtractionConfig config) {
        Preconditions.checkNotNull(config);
        INSTANCE.set(config);
    }
    
    public static ExtractionConfig get() {
        return INSTANCE.get();
    }

    public static void remove() {
        INSTANCE.remove();
    }    
    
}
