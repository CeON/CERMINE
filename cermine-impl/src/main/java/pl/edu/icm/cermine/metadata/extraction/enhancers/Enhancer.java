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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.Set;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * @author Krzysztof Rusek
 */
public interface Enhancer {

    /**
     * Extracts metadata from the document and puts it into metadata object.
     *
     * @param document segmented and classified document
     * @param metadata metadata object containing metadata already extracted
     * by other enhancers; newly extracted metadata are added to this object
     * @param enhancedFields set of fields enhanced already enhanced by other
     * enhancers; newly enhanced fields are added be added to this set
     */
    void enhanceMetadata(BxDocument document, DocumentMetadata metadata, Set<EnhancedField> enhancedFields);
}
