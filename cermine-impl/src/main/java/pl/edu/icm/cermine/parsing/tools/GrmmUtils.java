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

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.parsing.model.Token;

/**
 * Utility class for exporting feature lists to GRMM input.
 *
 * @author Bartosz Tarnawski
 */
public class GrmmUtils {

    private static final String LABEL_SEPARATOR = " ---- ";
    private static final String SEPARATOR = " ";
    private static final String START_LABEL = "Start";
    private static final String END_LABEL = "End";
    private static final String CONNECTOR = "@";

    /**
     * @param label label
     * @param features features
     * @return GRMM 'timestep' representing a token with the label and the
     * features
     */
    public static String toGrmmInput(String label, List<String> features) {
        StringBuilder builder = new StringBuilder();
        builder.append(label);
        builder.append(LABEL_SEPARATOR);
        for (int i = 0; i < features.size(); i++) {
            builder.append(features.get(i));
            if (i + 1 < features.size()) {
                builder.append(SEPARATOR);
            }
        }
        return builder.toString();
    }

    private static <T extends Token<?>> List<String>
            neighborFeatures(int current, int offset, List<T> tokens) {
        List<String> features = new ArrayList<String>();
        String suffix = "";
        if (offset != 0) {
            suffix = CONNECTOR + offset;
        }
        int neighbor = current + offset;
        if (neighbor < 0) {
            features.add(START_LABEL + suffix);
        } else if (neighbor >= tokens.size()) {
            features.add(END_LABEL + suffix);
        } else {
            for (String feature : tokens.get(neighbor).getFeatures()) {
                features.add(feature + suffix);
            }
        }
        return features;
    }

    /**
     * @param <T> token type
     * @param tokens the tokens whose feature lists should be exported
     * @param neighborInfluenceThreshold the maximum distance of token's
     * neighbor whose local features will be added to the token's feature list
     * @return GRMM input string representing the token sequence
     */
    public static <T extends Token<?>> String toGrmmInput(List<T> tokens,
            int neighborInfluenceThreshold) {
        StringBuilder grmmInputBuilder = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            List<String> features = new ArrayList<String>();
            String label = tokens.get(i).getLabel().toString();

            // For better readability, we write the own features of a token first.
            features.addAll(neighborFeatures(i, 0, tokens));
            for (int j = -neighborInfluenceThreshold; j <= neighborInfluenceThreshold; j++) {
                if (j != 0) {
                    features.addAll(neighborFeatures(i, j, tokens));
                }
            }

            grmmInputBuilder.append(GrmmUtils.toGrmmInput(label, features));
            grmmInputBuilder.append("\n");
        }

        return grmmInputBuilder.toString();
    }
}
