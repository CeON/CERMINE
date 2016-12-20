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
package pl.edu.icm.cermine.parsing.features;

import pl.edu.icm.cermine.parsing.model.ParsableString;
import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * Feature calculator which checks whether a token (representing a word) has a
 * given feature.
 *
 * @author Bartosz Tarnawski
 */
public abstract class BinaryTokenFeatureCalculator extends
        FeatureCalculator<Token<?>, ParsableString<?>> {

    /**
     * @param token token
     * @param context context
     * @return whether the token in the context has the feature represented by
     * the class
     */
    public abstract boolean calculateFeaturePredicate(Token<?> token, ParsableString<?> context);

    @Override
    public double calculateFeatureValue(Token<?> token, ParsableString<?> context) {
        return calculateFeaturePredicate(token, context) ? 1 : 0;
    }

}
