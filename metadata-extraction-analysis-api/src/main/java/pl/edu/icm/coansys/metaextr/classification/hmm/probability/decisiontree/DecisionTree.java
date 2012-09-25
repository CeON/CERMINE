package pl.edu.icm.coansys.metaextr.classification.hmm.probability.decisiontree;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureVector;

/**
 * Decision tree node interface.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 * @param <S> A type of labels.
 */
public interface DecisionTree<S> {

    /**
     * Gets left child of the node.
     *
     * @return Left child of the node.
     */
    DecisionTree<S> getLeft();

    /**
     * Gets right child of the node.
     *
     * @return Right child of the node.
     */
    DecisionTree<S> getRight();

    /**
     * Checks whether the node is a leaf.
     *
     * @return True if the node is a leaf, false otherwise.
     */
    boolean isLeaf();

    /**
     * Checks whether given feature vector goes to the left child in
     * the decision process.
     *
     * @param features Classified feature vector
     * @return True if the vector should go to the left child, false otherwise.
     */
    boolean isClassifiedLeft(FeatureVector features);

    /**
     * Checks whether given feature vector goes to the right child in
     * the decision process.
     *
     * @param features Classified feature vector
     * @return True if the vector should go to the right child, false otherwise.
     */
    boolean isClassifiedRight(FeatureVector features);

    /**
     * Gets the amount of training elements visiting the node
     * in the decision process, that have given label.
     *
     * @param label The label.
     * @return The amount of training elements.
     */
    int getLabelCount(S label);

}
