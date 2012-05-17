package pl.edu.icm.yadda.analysis.relations;

/**
 * Clusterizes items according to a similarity matrix.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
public interface Clusterizer extends Cloneable{

    /**
     * Clusterizes items according to a similarity matrix.
     * 
     * @param similarities
     *            Lower triangular matrix of similarities between items.
     *            Positive values in the matrix represent degree of similarity,
     *            negative values represent degree of dissimilarity. It is a
     *            lower triangular matrix, so similarities[i][j] is present only
     *            if i > j.
     * @return An array of length equal to the number of clustered items. The
     *         i-th value holds the identifier of the cluster holding the i-th
     *         item. In other words, i-th and j-th items are in the same cluster
     *         if and only if the i-th and j-th values in the array are equal.
     *         No assumptions about the values of the cluster identifiers should
     *         be made.
     */
    int[] clusterize(double[][] similarities);
}
