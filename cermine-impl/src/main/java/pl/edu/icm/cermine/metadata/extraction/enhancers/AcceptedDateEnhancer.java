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

import pl.edu.icm.cermine.metadata.model.DocumentDate;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;

/**
 * @author Krzysztof Rusek
 */
public class AcceptedDateEnhancer extends AbstractDateEnhancer {

    public AcceptedDateEnhancer() {
        super(EnhancedField.ACCEPTED_DATE, "accepted");
    }

    @Override
    protected void enhanceMetadata(DocumentMetadata metadata, String day, String month, String year) {
        metadata.setDate(DocumentDate.DATE_ACCEPTED, day, month, year);
    }

}
