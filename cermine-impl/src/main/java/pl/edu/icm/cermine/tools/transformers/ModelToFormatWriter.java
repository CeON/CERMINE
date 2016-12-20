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

import java.io.Writer;
import java.util.List;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * Interface for writers of model objects.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <T> the type of model
 */
public interface ModelToFormatWriter<T> {
    
    /**
     * Writes a model object to a string.
     * 
     * @param object a model object
     * @param hints additional hints used during the conversion
     * @return written object
     * @throws TransformationException TransformationException
     */
    String write(T object, Object... hints) throws TransformationException;
    
    /**
     * Writes a list of model objects to a string.
     * 
     * @param objects a list of model objects
     * @param hints additional hints used during the conversion
     * @return written object
     * @throws TransformationException TransformationException
     */
    String writeAll(List<T> objects, Object... hints) throws TransformationException;
    
    /**
     * Writes a model object using the given writer.
     * 
     * @param writer writer
     * @param object a model object
     * @param hints additional hints used during the conversion
     * @throws TransformationException TransformationException
     */
    void write(Writer writer, T object, Object... hints) throws TransformationException;

    /**
     * Writes a list of model objects using the given writer.
     * 
     * @param writer writer
     * @param objects a list of model objects
     * @param hints additional hints used during the conversion
     * @throws TransformationException TransformationException
     */
    void writeAll(Writer writer, List<T> objects, Object... hints) throws TransformationException;
    
}
