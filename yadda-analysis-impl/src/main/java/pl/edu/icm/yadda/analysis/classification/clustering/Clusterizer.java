package pl.edu.icm.yadda.analysis.classification.clustering;

/**
 *
 * @author Dominika Tkaczyk
 */
public interface Clusterizer {
    
    public int[] clusterize(double distanceMatrix[][], double maxDistance);
   
}

