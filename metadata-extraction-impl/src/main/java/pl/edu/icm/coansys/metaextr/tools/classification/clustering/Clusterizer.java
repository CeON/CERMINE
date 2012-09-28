package pl.edu.icm.coansys.metaextr.tools.classification.clustering;

/**
 *
 * @author Dominika Tkaczyk
 */
public interface Clusterizer {
    
    public int[] clusterize(double distanceMatrix[][], double maxDistance);
   
}

