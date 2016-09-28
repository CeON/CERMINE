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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ResourcesReader {

    public static List<String> readLinesAsList(String file) throws TransformationException {
        return readLinesAsList(file, ID_TRANSFORMER);
    }
    
    public static List<String> readLinesAsList(String file, StringTransformer transformer) 
            throws TransformationException {
        List<String> lines = new ArrayList<String>();
        InputStream is = ResourcesReader.class.getResourceAsStream(file);
        if (is == null) {
            throw new TransformationException("Resource not found: " + file);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                lines.add(transformer.transform(line));
            }
        } catch (IOException ex) {
            throw new TransformationException(ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                throw new TransformationException(ex);
            }
        }
        return lines;
    }
    
    public interface StringTransformer {
        String transform(String original);
    }
    
    public static final StringTransformer ID_TRANSFORMER = new StringTransformer() {
        @Override
        public String transform(String original) {
            return original;
        }
    };
    
    public static final StringTransformer TRIM_TRANSFORMER = new StringTransformer() {
        @Override
        public String transform(String original) {
            return original.trim();
        }
    };

    private ResourcesReader() {
    }
    
}
