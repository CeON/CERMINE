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
            double mind = Double.POSITIVE_INFINITY;
            Set<Integer> minClust1 = null;
            Set<Integer> minClust2 = null;
            for (Set<Integer> clust1 : clusters) {
                for (Set<Integer> clust2 : clusters) {
                    if (clust1.equals(clust2)) {
                        continue;
                    }
                    double maxd = Double.NEGATIVE_INFINITY;
                    for (int i : clust1) {
                        for (int j : clust2) {
                            if (distanceMatrix[i][j] > maxd) {
                                maxd = distanceMatrix[i][j];
                            }
                        }
                    }
                    if (maxd < mind) {
                        mind = maxd;
                        minClust1 = clust1;
                        minClust2 = clust2;
                    }
                }
            }
            if (mind < maxDistance) {
                clusters.remove(minClust1);
                clusters.remove(minClust2);
                minClust1.addAll(minClust2);
                clusters.add(minClust1);
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

