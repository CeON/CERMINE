package pl.edu.icm.cermine.tools.classification.clustering;

/**
 *
 * @author Dominika Tkaczyk
 */
public interface Clusterizer {
    
    int[] clusterize(double distanceMatrix[][], double maxDistance);
   
}

