package pl.edu.icm.cermine.tools.classification.hmm.model;

import java.util.*;
import pl.edu.icm.cermine.structure.tools.ProbabilityDistribution;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

/**
 * Builds a decision tree using a collection of training elements.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public final class DecisionTreeBuilder {

    /* This field causes the expansion of tree nodes to be stopped, when there 
     * are less that stopExpanding training elements arriving to the node.
     */
    private static int stopExpanding = 20;

    private DecisionTreeBuilder() {}
    
    /**
     * Builds a decision tree from a training set.
     *
     * @param <S> A type of element decisions (labels).
     * @param trainingSet A set of training elements.
     * @param attributes A set of feature names that can be used.
     * @return Root of the built decision tree.
     */
    public static <S extends Comparable<S>> DecisionTree<S> buildDecisionTree(
            Set<TrainingElement<S>> trainingSet, Set<String> attributes) {
        return DecisionTreeBuilder.buildDecisionTree(trainingSet, attributes, stopExpanding);
    }

    /**
     * Builds a decision tree from a training set with a given stopExpanding
     * parameter.
     *
     * @param <S> A type of element decisions (labels).
     * @param trainingSet A set of training elements.
     * @param attributes A set of feature names that can be used.
     * @param stopExpanding stopExpanding parameter.
     * @return Root of the built decision tree.
     */
    public static <S extends Comparable<S>> SimpleDecisionTree<S> buildDecisionTree(
            Set<TrainingElement<S>> trainingSet, Set<String> attributes, int stopExpanding) {
        return constructNode(trainingSet, attributes, stopExpanding);
    }

    /**
     * Constructs a decision tree node from a training set.
     *
     * @param <S> A type of element decisions (labels).
     * @param trainingSet A set of training elements.
     * @param attributes A set of attributes (feature vector indexes) thet can 
     * stil be used in decisions.
     * @param stopExpanding stopExpanding parameter.
     * @return Constructed node.
     */
    private static <S extends Comparable<S>> SimpleDecisionTree<S> constructNode(
            Set<TrainingElement<S>> trainingSet, Set<String> attributes, int stopExpanding) {
        if (trainingSet.isEmpty()) {
            return null;
        }

        ProbabilityDistribution<S> probDistribution = new ProbabilityDistribution<S>();
        for (TrainingElement<S> element : trainingSet) {
            probDistribution.addEvent(element.getLabel());
        }
        if (probDistribution.getEvents().size() == 1 || attributes.isEmpty() || trainingSet.size() < stopExpanding) {
            return new SimpleDecisionTree<S>(probDistribution);
        }

        NodeDecision decision = chooseDecision(trainingSet, attributes);
        if (decision == null) {
            return new SimpleDecisionTree<S>(probDistribution);
        }

        Set<String> newAttributes = new HashSet<String>(attributes);
        newAttributes.remove(decision.testedFeature);

        Set<TrainingElement<S>> leftElements = new HashSet<TrainingElement<S>>();
        Set<TrainingElement<S>> rightElements = new HashSet<TrainingElement<S>>();

        for (TrainingElement<S> element : trainingSet) {
            if (decision.isLeft(element.getObservation())) {
                leftElements.add(element);
            } else {
                rightElements.add(element);
            }
        }

        SimpleDecisionTree<S> leftNode = constructNode(leftElements, newAttributes, stopExpanding);
        SimpleDecisionTree<S> rightNode = constructNode(rightElements, newAttributes, stopExpanding);

        return new SimpleDecisionTree<S>(probDistribution, leftNode, rightNode, decision.testedFeature, decision.cut);
    }

    /**
     * Chooses the best decision (feature vector index and cut) for given
     * training set.
     *
     * @param <S> A type of element decisions (labels).
     * @param trainingSet A set of training elements.
     * @param attributes A set of attributes (feature vector indexes) thet can
     * stil be use in decisions.
     * @return The best decision.
     */
    private static <S extends Comparable<S>> NodeDecision chooseDecision(
            Set<TrainingElement<S>> trainingSet, Set<String> attributes) {
        String bestAttribute = null;
        double bestCut = -1;
        double bestEntropyGain = 0;

        List<TrainingElement<S>> trainingList =
                new ArrayList<TrainingElement<S>>(trainingSet);

        for (String attribute : attributes) {
            final String sortAttribute = attribute;
            Collections.sort(trainingList, new Comparator<TrainingElement<S>>() {

                @Override
                public int compare(TrainingElement<S> t,  TrainingElement<S> t1) {
                    TrainingElement<S> te1 = (TrainingElement<S>) t;
                    TrainingElement<S> te2 = (TrainingElement<S>) t1;
                    int ret = Double.compare(te1.getObservation().getFeature(sortAttribute),
                            te2.getObservation().getFeature(sortAttribute));
                    if (ret == 0) {
                        ret = te1.getLabel().compareTo(te2.getLabel());
                    }
                    return ret;
                }
            });

            ProbabilityDistribution<S> leftLabelsProb = new ProbabilityDistribution<S>();
            ProbabilityDistribution<S> rightLabelsProb = new ProbabilityDistribution<S>();
            for (TrainingElement<S> element : trainingList) {
                rightLabelsProb.addEvent(element.getLabel());
            }

            int leftCount = 0;
            for (int i = 0; i < trainingList.size() - 1; i++) {

                TrainingElement<S> trainingElement1 = trainingList.get(i);
                TrainingElement<S> trainingElement2 = trainingList.get(i + 1);
                S label1 = trainingElement1.getLabel();
                S label2 = trainingElement2.getLabel();

                if (leftCount <= i) {
                    double feature = trainingList.get(leftCount).getObservation().getFeature(attribute);
                    while (leftCount < trainingList.size()
                            && trainingList.get(leftCount).getObservation().getFeature(attribute) == feature) {
                        leftLabelsProb.addEvent(trainingList.get(leftCount).getLabel());
                        rightLabelsProb.removeEvent(trainingList.get(leftCount).getLabel());
                        leftCount++;
                    }
                }

                if (label1.equals(label2)) {
                    double leftEntropy = leftLabelsProb.getEntropy();
                    double rightEntropy = rightLabelsProb.getEntropy();

                    double entropyGain = leftEntropy * (double) leftCount / (double) trainingList.size()
                            + rightEntropy * (double) (trainingList.size() - leftCount) / (double) (trainingList.size());

                    if (bestAttribute == null || entropyGain < bestEntropyGain) {
                        double f1 = trainingElement1.getObservation().getFeature(attribute);
                        double f2 = trainingElement2.getObservation().getFeature(attribute);
                        if (f1 != f2 ||
                              f1 != trainingList.get(trainingList.size() - 1).getObservation().getFeature(attribute)) {
                            bestAttribute = attribute;
                            bestCut = (f1 + f2) / 2;
                            bestEntropyGain = entropyGain;
                        }
                    }
                }
            }
        }

        if (bestAttribute == null) {
            return null;
        }

        return new NodeDecision(bestAttribute, bestCut);
    }


    private static class NodeDecision {

        private String testedFeature;
        private double cut;

        public NodeDecision(String testedFeature, double cut) {
            this.testedFeature = testedFeature;
            this.cut = cut;
        }

        public boolean isLeft(FeatureVector features) {
            return features.getFeature(testedFeature) <= cut;
        }

        public boolean isRight(FeatureVector features) {
            return features.getFeature(testedFeature) > cut;
        }

    }
}
