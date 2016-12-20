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

package pl.edu.icm.cermine.tools.transformers;

import java.io.Reader;
import java.util.List;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * Interface for readers of model objects.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <T> the type of model
 */
public interface FormatToModelReader<T> {
    
    /**
     * Reads the format into the model object.
     * 
     * @param string input object in a certain format
     * @param hints additional hints used during the conversion
     * @return a model object
     * @throws TransformationException TransformationException
     */
    T read(String string, Object... hints) throws TransformationException;
    
    /**
     * Reads the format into the list of model objects.
     * 
     * @param string input object in a certain format
     * @param hints additional hints used during the conversion
     * @return a list of model object
     * @throws TransformationException TransformationException
     */
    List<T> readAll(String string, Object... hints) throws TransformationException;
    
    /**
     * Reads the format into the model object.
     * 
     * @param reader input reader
     * @param hints additional hints used during the conversion
     * @return a model object
     * @throws TransformationException TransformationException
     */
    T read(Reader reader, Object... hints) throws TransformationException;

    /**
     * Reads the format into the model object.
     * 
     * @param reader input reader
     * @param hints additional hints used during the conversion
     * @return a list of model objects
     * @throws TransformationException TransformationException
     */
    List<T> readAll(Reader reader, Object... hints) throws TransformationException;
}
