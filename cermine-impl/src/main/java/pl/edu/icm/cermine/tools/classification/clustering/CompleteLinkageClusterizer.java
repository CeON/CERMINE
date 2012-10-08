package pl.edu.icm.cermine.tools.classification.clustering;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Dominika Tkaczyk
 */
public class CompleteLinkageClusterizer implements Clusterizer {
    
    @Override
    public int[] clusterize(double distanceMatrix[][], double maxDistance) {
        Set<Set<Integer>> clusters = new HashSet<Set<Integer>>();
        for (int i = 0; i < distanceMatrix.length; i++) {
            clusters.add(Sets.newHashSet(i));
        }
        
        while (true) {
            double min_d = Double.POSITIVE_INFINITY;
            Set<Integer> min_clust1 = null;
            Set<Integer> min_clust2 = null;
            for (Set<Integer> clust1 : clusters) {
                for (Set<Integer> clust2 : clusters) {
                    if (clust1.equals(clust2)) {
                        continue;
                    }
                    double max_d = Double.NEGATIVE_INFINITY;
                    for (int i : clust1) {
                        for (int j : clust2) {
                            if (distanceMatrix[i][j] > max_d) {
                                max_d = distanceMatrix[i][j];
                            }
                        }
                    }
                    if (max_d < min_d) {
                        min_d = max_d;
                        min_clust1 = clust1;
                        min_clust2 = clust2;
                    }
                }
            }
            if (min_d < maxDistance) {
                clusters.remove(min_clust1);
                clusters.remove(min_clust2);
                min_clust1.addAll(min_clust2);
                clusters.add(min_clust1);
            } else {
                break;
            }
        }
        
        int[] clusterArray = new int[distanceMatrix.length];
        int i = 0;
        for (Set<Integer> cl : clusters) {
            for (int h : cl) {
                clusterArray[h] = i;
            }
            i++;
        }
        
        return clusterArray;
    }
    
}

