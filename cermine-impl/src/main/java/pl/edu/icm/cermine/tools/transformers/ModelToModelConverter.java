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

import java.util.List;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * Interface for converters between models.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <S> type of input model
 * @param <T> type of output model
 */
public interface ModelToModelConverter<S, T> {
    
    /**
     * Converts source model into the target model.
     * 
     * @param source the source object
     * @param hints additional hints used during the conversion
     * @return the converted object
     * @throws TransformationException TransformationException
     */
    T convert(S source, Object... hints) throws TransformationException;
    
    /**
     * Converts source model into the target model.
     * 
     * @param source the list of source objects
     * @param hints additional hints used during the conversion
     * @return the list of converted objects
     * @throws TransformationException TransformationException
     */
    List<T> convertAll(List<S> source, Object... hints) throws TransformationException;
}
