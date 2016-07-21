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

package pl.edu.icm.cermine.tools.distance;

import com.google.common.collect.Sets;
import java.util.List;
import pl.edu.icm.cermine.tools.classification.general.FeatureVector;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class FeatureVectorEuclideanMetric implements FeatureVectorDistanceMetric {

    @Override
    public double getDistance(FeatureVector vector1, FeatureVector vector2) {
        double sum = 0;
        List<String> featureNames1 = vector1.getFeatureNames();
        List<String> featureNames2 = vector2.getFeatureNames();
        
        if (Sets.newHashSet(featureNames1).equals(Sets.newHashSet(featureNames2))) {
            for (String feature : featureNames1) {
                sum += Math.pow(vector1.getValue(feature) - vector2.getValue(feature), 2);
            }
        } else {
            for (int i = 0; i < vector1.size(); i++) {
                sum += Math.pow(vector1.getValue(i) - vector2.getValue(i), 2);
            }
        }

        return Math.sqrt(sum);
    }
}
