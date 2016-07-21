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

package pl.edu.icm.cermine.tools.classification.clustering;

import java.util.ArrayList;
import pl.edu.icm.cermine.tools.classification.general.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class FeatureVectorsNormalizer {
    
    public static void normalize(FeatureVector[] vectors, FeatureVectorBuilder builder) {
        for (String feature : (ArrayList<String>)builder.getFeatureNames()) {
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            
            for (FeatureVector vector : vectors) {
                if (vector.getValue(feature) < min) {
                    min = vector.getValue(feature);
                }
                if (vector.getValue(feature) > max) {
                    max = vector.getValue(feature);
                }
            }
            
            for (FeatureVector vector : vectors) {
                if (max - min == 0) {
                    vector.addFeature(feature, 0);
                } else {
                    vector.addFeature(feature, (vector.getValue(feature) - min) / (max - min));
                }
            }
        }
    }

    private FeatureVectorsNormalizer() {}
    
}
