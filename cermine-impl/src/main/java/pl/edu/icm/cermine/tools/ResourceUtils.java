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

package pl.edu.icm.cermine.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang.StringUtils;

/**
 * @author madryk
 */
public class ResourceUtils {

    private static final String CLASSPATH_RESOURCE_PREFIX = "classpath:";
    
    public static InputStream openResourceStream(String resourcePath) throws IOException {

        if (StringUtils.startsWith(resourcePath, CLASSPATH_RESOURCE_PREFIX)) {
            InputStream inputStream = ResourceUtils.class.getResourceAsStream(resourcePath.substring(CLASSPATH_RESOURCE_PREFIX.length()));
            
            return inputStream;
        }
        return new FileInputStream(resourcePath);
    }
}
