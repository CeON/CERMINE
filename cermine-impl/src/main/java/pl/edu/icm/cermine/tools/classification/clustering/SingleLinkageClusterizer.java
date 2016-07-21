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

/**
 * Single linkage clusterizer.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SingleLinkageClusterizer implements Clusterizer {
    
    @Override
    public int[] clusterize(double distanceMatrix[][], double maxDistance) {
        int[] clusters = new int[distanceMatrix.length];
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = i;
        }
        
        while (true) {
            int mini = -1;
            int minj = -1;
            for (int k = 0; k < distanceMatrix.length; k++) {
                for (int l = 0; l < distanceMatrix.length; l++) {
                    if (distanceMatrix[k][l] < maxDistance && clusters[k] != clusters[l]) {
                        mini = k;
                        minj = l;
                    }
                }
            }
            
            if (mini == -1) {
                return clusters;
            }
            
            int old = clusters[mini];

            for (int i = 0; i < clusters.length; i++) {
                if (clusters[i] == old) {
                    clusters[i] = clusters[minj];
                }
            }
        }
    }

}

