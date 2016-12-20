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

import java.util.List;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.parsing.model.Token;

/**
 * Class for predicting token labels.
 *
 * @author Bartosz Tarnawski
 * @param <T> token type
 */
public interface TokenClassifier<T extends Token<?>> {

    /**
     * Predicts and sets a label for each of the tokens. The tokens are assumed
     * to hold appropriate lists of features.
     *
     * @param tokens tokens
     * @throws AnalysisException AnalysisException
     */
    void classify(List<T> tokens) throws AnalysisException;
}
