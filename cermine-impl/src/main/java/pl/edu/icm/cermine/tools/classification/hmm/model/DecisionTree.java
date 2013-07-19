package pl.edu.icm.cermine.tools.classification.hmm.model;

import pl.edu.icm.cermine.structure.tools.ProbabilityDistribution;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 * Decision tree node interface.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 * @param <T> A type of labels.
 */
public class DecisionTree<T> {
    
    private DecisionTree<T> left;
    private DecisionTree<T> right;
    private ProbabilityDistribution<T> labelsProbability;
    private String featureName;
    private double featureCut;


    public DecisionTree(ProbabilityDistribution<T> labelsProbability) {
        this.labelsProbability = labelsProbability;
    }

    public DecisionTree(ProbabilityDistribution<T> labelsProbability,
            DecisionTree<T> left, DecisionTree<T> right, String featureName, double featureCut) {
        this(labelsProbability);
        this.left = left;
        this.right = right;
        this.featureName = featureName;
        this.featureCut = featureCut;
    }

    public DecisionTree<T> getLeft() {
        return left;
    }

    public DecisionTree<T> getRight() {
        return right;
    }

    public boolean isLeaf() {
        return (left == null && right == null);
    }

    public boolean isClassifiedLeft(FeatureVector features) {
        return !isLeaf() && features.getFeatureValue(featureName) <= featureCut;
    }

    public boolean isClassifiedRight(FeatureVector features) {
        return !isLeaf() && features.getFeatureValue(featureName) > featureCut;
    }

    public int getLabelCount(T label) {
        return labelsProbability.getEventCount(label);
    }

}
