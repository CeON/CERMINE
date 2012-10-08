package pl.edu.icm.cermine.tools.classification.hmm.model;

import pl.edu.icm.cermine.structure.tools.ProbabilityDistribution;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 * Simple decision tree implementation.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class SimpleDecisionTree<T> implements DecisionTree<T> {

    private SimpleDecisionTree<T> left;
    private SimpleDecisionTree<T> right;
    private ProbabilityDistribution<T> labelsProbability;
    private String featureName;
    private double featureCut;


    public SimpleDecisionTree(ProbabilityDistribution<T> labelsProbability) {
        this.labelsProbability = labelsProbability;
    }

    public SimpleDecisionTree(ProbabilityDistribution<T> labelsProbability,
            SimpleDecisionTree<T> left, SimpleDecisionTree<T> right, String featureName, double featureCut) {
        this(labelsProbability);
        this.left = left;
        this.right = right;
        this.featureName = featureName;
        this.featureCut = featureCut;
    }

    @Override
    public SimpleDecisionTree<T> getLeft() {
        return left;
    }

    @Override
    public SimpleDecisionTree<T> getRight() {
        return right;
    }

    @Override
    public boolean isLeaf() {
        return (left == null && right == null);
    }

    @Override
    public boolean isClassifiedLeft(FeatureVector features) {
        return !isLeaf() && features.getFeature(featureName) <= featureCut;
    }

    @Override
    public boolean isClassifiedRight(FeatureVector features) {
        return !isLeaf() && features.getFeature(featureName) > featureCut;
    }

    @Override
    public int getLabelCount(T label) {
        return labelsProbability.getEventCount(label);
    }
}
