package pl.edu.icm.cermine.bibref.extraction.tools;

import pl.edu.icm.cermine.tools.classification.clustering.ClusteringEvaluator;

/**
 *
 * @author Dominika Tkaczyk
 */
public class BibRefLinesClusteringEvaluator implements ClusteringEvaluator {
    
    public static final int DEFAULT_MAX_REF_LINES = 10;
    
    private int maxRefLinesCount = DEFAULT_MAX_REF_LINES;

    @Override
    public boolean isAcceptable(int[] clusters) {
        int first = clusters[0];

        int prevIndex = 0;
        for (int index = 0; index < clusters.length; index++) {
            if (first == clusters[index]) {
                if (index - prevIndex > maxRefLinesCount) {
                    return false;
                }
                prevIndex = index;
            }
        }
        if (clusters.length - prevIndex > maxRefLinesCount) {
            return false;
        }
        
        return true;
    }

    public int getMaxRefLinesCount() {
        return maxRefLinesCount;
    }

    public void setMaxRefLinesCount(int maxRefLinesCount) {
        this.maxRefLinesCount = maxRefLinesCount;
    }
    
}
