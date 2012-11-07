package pl.edu.icm.cermine.tools.classification.clustering;

/**
 * Interface for classes evaluating the effects of clustering.
 * 
 * @author Dominika Tkaczyk
 */
public interface ClusteringEvaluator {
    
    /**
     * Checks, whether the effects of clustering are acceptable
     * 
     * @param clusters
     * @return 
     */
    boolean isAcceptable(int[] clusters);
    
}
