package pl.edu.icm.coansys.metaextr.classification.clustering;

/**
 *
 * @author Dominika Tkaczyk
 */
public interface Clusterizer {
    
    public int[] clusterize(double distanceMatrix[][], double maxDistance);
   
}

