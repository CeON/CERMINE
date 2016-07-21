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
package pl.edu.icm.cermine.parsing.tools;

import pl.edu.icm.cermine.parsing.model.ParsableString;

/**
 * Finds features of tokens in a tokenized parsable string.
 *
 * @author Bartosz Tarnawski
 * @param <PS> type of the parsable string to process
 */
public interface FeatureExtractor<PS extends ParsableString<?>> {

    /**
     * Adds appropriate strings representing features to the tokens of the
     * parsable string.
     *
     * @param parsableString the tokenized parsable string to be processed
     */
    void calculateFeatures(PS parsableString);
}
