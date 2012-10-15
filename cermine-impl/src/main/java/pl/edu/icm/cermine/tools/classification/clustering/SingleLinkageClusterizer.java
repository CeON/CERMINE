package pl.edu.icm.cermine.tools.classification.clustering;

/**
 *
 * @author Dominika Tkaczyk
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

