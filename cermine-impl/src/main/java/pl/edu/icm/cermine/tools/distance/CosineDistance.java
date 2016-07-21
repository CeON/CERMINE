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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Pawel Szostek
 */
public class CosineDistance {

    private Map<String, Integer> calculateVector(List<String> tokens) {
        HashMap<String, Integer> vector = new HashMap<String, Integer>();
        for (String token : tokens) {
            if (vector.containsKey(token)) {
                vector.put(token, vector.get(token) + 1);
            } else {
                vector.put(token, 1);
            }
        }
        return vector;
    }

    private double vectorLength(Map<String, Integer> vector) {
        double ret = 0.0;
        for (Entry<String, Integer> entry : vector.entrySet()) {
            ret += entry.getValue() * entry.getValue();
        }
        return Math.sqrt(ret);
    }

    private double dotProduct(Map<String, Integer> vector1, Map<String, Integer> vector2) {
        double ret = 0.0;
        for (Entry<String, Integer> entry : vector1.entrySet()) {
            if (vector2.containsKey(entry.getKey())) {
                ret += entry.getValue() * vector2.get(entry.getKey());
            }
        }
        return ret;
    }

    public double compare(List<String> s1, List<String> s2) {
        Map<String, Integer> v1 = calculateVector(s1);
        Map<String, Integer> v2 = calculateVector(s2);

        return dotProduct(v1, v2) / (vectorLength(v1) * vectorLength(v2));
    }
}
