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

import pl.edu.icm.cermine.tools.classification.general.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.distance.FeatureVectorDistanceMetric;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class FeatureVectorClusterizer {
   
    private Clusterizer clusterizer;
    
    public int[] clusterize(FeatureVector[] vectors, FeatureVectorBuilder builder, FeatureVectorDistanceMetric metric, 
            double maxDistance, boolean normalize) {
        if (normalize){
            FeatureVectorsNormalizer.normalize(vectors, builder);
        }
        double distanceMatrix[][] = new double[vectors.length][vectors.length];
        for (int i = 0; i < vectors.length; i++) {
            for (int j = 0; j < vectors.length; j++) {
                distanceMatrix[i][j] = metric.getDistance(vectors[i], vectors[j]);
                distanceMatrix[j][i] = metric.getDistance(vectors[i], vectors[j]);
            }
        }
        
        return clusterizer.clusterize(distanceMatrix, maxDistance);
    }

    public void setClusterizer(Clusterizer clusterizer) {
        this.clusterizer = clusterizer;
    }
    
}
