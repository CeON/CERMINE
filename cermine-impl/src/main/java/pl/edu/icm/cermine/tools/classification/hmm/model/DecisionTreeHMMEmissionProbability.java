package pl.edu.icm.cermine.tools.classification.hmm.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

/**
 * Hidden Markov Model emission probability implementation based on a decision
 * tree.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DecisionTreeHMMEmissionProbability<S extends Comparable<S>> implements HMMEmissionProbability<S> {

    private DecisionTree<S> decisionTree;

    private double zeroProbabilityValue;

    public DecisionTreeHMMEmissionProbability(List<TrainingElement<S>> trainingElements,
                                              Set<String> featureNames) {
        this(trainingElements, featureNames, 0.0);
    }

    public DecisionTreeHMMEmissionProbability(List<TrainingElement<S>> trainingElements, Set<String> featureNames, int decisionTreeExpand) {
        this(trainingElements, featureNames, decisionTreeExpand, 0.0);
    }

    public DecisionTreeHMMEmissionProbability(List<TrainingElement<S>> trainingElements,
                                              Set<String> featureNames, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        decisionTree = DecisionTreeBuilder.buildDecisionTree(new HashSet<TrainingElement<S>>(trainingElements), featureNames);
    }

    public DecisionTreeHMMEmissionProbability(List<TrainingElement<S>> trainingElements,
                                              Set<String> featureNames, int decisionTreeExpand, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        decisionTree = DecisionTreeBuilder.buildDecisionTree(new HashSet<TrainingElement<S>>(trainingElements), featureNames, decisionTreeExpand);
    }

    public DecisionTree<S> getDecisionTree() {
    	return this.decisionTree;
    }
    
    @Override
    public double getProbability(S label, FeatureVector featureVector) {
        DecisionTree<S> node = decisionTree;
        while (node != null && !node.isLeaf()) {
            if (node.isClassifiedLeft(featureVector)) {
                node = node.getLeft();
            } else {
                node = node.getRight();
            }
        }

        if (node == null || decisionTree.getLabelCount(label) == 0 || node.getLabelCount(label) == 0) {
            return zeroProbabilityValue;
        }
        return (double) node.getLabelCount(label) / (double) decisionTree.getLabelCount(label);
    }

}
