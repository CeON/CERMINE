package pl.edu.icm.yadda.analysis.hmm.probability.decisiontree;

import pl.edu.icm.yadda.analysis.hmm.features.FeatureVector;
import pl.edu.icm.yadda.analysis.textr.tools.ProbabilityDistribution;

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
    public SimpleDecisionTree getLeft() {
        return left;
    }

    @Override
    public SimpleDecisionTree getRight() {
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
