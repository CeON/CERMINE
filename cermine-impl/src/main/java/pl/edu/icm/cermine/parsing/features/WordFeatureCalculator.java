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

import java.util.List;
import pl.edu.icm.cermine.parsing.model.ParsableString;
import pl.edu.icm.cermine.parsing.model.Token;

/**
 * A 'word feature' of a token is a binary feature identifying the token's text.
 *
 * @author Bartosz Tarnawski
 */
public class WordFeatureCalculator {

    private final List<BinaryTokenFeatureCalculator> blockingFeatures;
    private final boolean toLowerCase;
    // This is a GRMM convention, see: https://dl.dropboxusercontent.com/u/55174954/grmm.htm
    private static final String PREFIX = "W=";

    /**
     * @param blockingFeatures the word feature will not be produced if the
     * given token has any of these features
     * @param toLowerCase whether the word feature should be converted to lower
     * case
     */
    public WordFeatureCalculator(List<BinaryTokenFeatureCalculator> blockingFeatures,
            boolean toLowerCase) {
        this.blockingFeatures = blockingFeatures;
        this.toLowerCase = toLowerCase;
    }

    /**
     * @param token token
     * @param context context
     * @return the word represented by the token in an appropriate format or
     * null if the token has a blocking feature
     */
    public String calculateFeatureValue(Token<?> token, ParsableString<?> context) {
        for (BinaryTokenFeatureCalculator feature : blockingFeatures) {
            if (feature.calculateFeaturePredicate(token, context)) {
                return null;
            }
        }
        if (toLowerCase) {
            return PREFIX + token.getText().toLowerCase();
        } else {
            return PREFIX + token.getText();
        }
    }
}
