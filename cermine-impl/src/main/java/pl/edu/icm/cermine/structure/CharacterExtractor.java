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

package pl.edu.icm.cermine.structure;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Interface for extracting individual characters along with their bounding boxes from a file. 
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface CharacterExtractor {
	
    /**
     * Extracts characters from the file.
     * 
     * @param stream PDF stream
     * @return a document containing pages with individual characters.
     * @throws AnalysisException AnalysisException
     */
    BxDocument extractCharacters(InputStream stream) throws AnalysisException;
}
