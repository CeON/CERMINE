package pl.edu.icm.cermine.tools.classification.hmm.model;

import pl.edu.icm.cermine.structure.tools.ProbabilityDistribution;
import java.util.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 * Builds a decision tree using a collection of training elements.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public final class DecisionTreeBuilder {
    
    public static final int DEFAULT_STOP_EXPANDING = 20;

    /* This field causes the expansion of tree nodes to be stopped, when there 
     * are less that stopExpanding training elements arriving to the node.
     */
    private static int stopExpanding = DEFAULT_STOP_EXPANDING;

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
            Set<HMMTrainingSample<S>> trainingSet, List<String> attributes) {
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
    public static <S extends Comparable<S>> DecisionTree<S> buildDecisionTree(
            Set<HMMTrainingSample<S>> trainingSet, List<String> attributes, int stopExpanding) {
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
    private static <S extends Comparable<S>> DecisionTree<S> constructNode(
            Set<HMMTrainingSample<S>> trainingSet, List<String> attributes, int stopExpanding) {
        if (trainingSet.isEmpty()) {
            return null;
        }

        ProbabilityDistribution<S> probDistribution = new ProbabilityDistribution<S>();
        for (HMMTrainingSample<S> element : trainingSet) {
            probDistribution.addEvent(element.getLabel());
        }
        if (probDistribution.getEvents().size() == 1 || attributes.isEmpty() || trainingSet.size() < stopExpanding) {
            return new DecisionTree<S>(probDistribution);
        }

        NodeDecision decision = chooseDecision(trainingSet, attributes);
        if (decision == null) {
            return new DecisionTree<S>(probDistribution);
        }

        List<String> newAttributes = new ArrayList<String>(attributes);
        newAttributes.remove(decision.testedFeature);

        Set<HMMTrainingSample<S>> leftElements = new HashSet<HMMTrainingSample<S>>();
        Set<HMMTrainingSample<S>> rightElements = new HashSet<HMMTrainingSample<S>>();

        for (HMMTrainingSample<S> element : trainingSet) {
            if (decision.isLeft(element.getObservation())) {
                leftElements.add(element);
            } else {
                rightElements.add(element);
            }
        }

        DecisionTree<S> leftNode = constructNode(leftElements, newAttributes, stopExpanding);
        DecisionTree<S> rightNode = constructNode(rightElements, newAttributes, stopExpanding);

        return new DecisionTree<S>(probDistribution, leftNode, rightNode, decision.testedFeature, decision.cut);
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
            Set<HMMTrainingSample<S>> trainingSet, List<String> attributes) {
        String bestAttribute = null;
        double bestCut = -1;
        double bestEntropyGain = 0;

        List<HMMTrainingSample<S>> trainingList =
                new ArrayList<HMMTrainingSample<S>>(trainingSet);

        for (String attribute : attributes) {
            final String sortAttribute = attribute;
            Collections.sort(trainingList, new Comparator<HMMTrainingSample<S>>() {

                @Override
                public int compare(HMMTrainingSample<S> t,  HMMTrainingSample<S> t1) {
                    HMMTrainingSample<S> te1 = (HMMTrainingSample<S>) t;
                    HMMTrainingSample<S> te2 = (HMMTrainingSample<S>) t1;
                    int ret = Double.compare(te1.getObservation().getFeatureValue(sortAttribute),
                            te2.getObservation().getFeatureValue(sortAttribute));
                    if (ret == 0) {
                        ret = te1.getLabel().compareTo(te2.getLabel());
                    }
                    return ret;
                }
            });

            ProbabilityDistribution<S> leftLabelsProb = new ProbabilityDistribution<S>();
            ProbabilityDistribution<S> rightLabelsProb = new ProbabilityDistribution<S>();
            for (HMMTrainingSample<S> element : trainingList) {
                rightLabelsProb.addEvent(element.getLabel());
            }

            int leftCount = 0;
            for (int i = 0; i < trainingList.size() - 1; i++) {

                HMMTrainingSample<S> trainingElement1 = trainingList.get(i);
                HMMTrainingSample<S> trainingElement2 = trainingList.get(i + 1);
                S label1 = trainingElement1.getLabel();
                S label2 = trainingElement2.getLabel();

                if (leftCount <= i) {
                    double feature = trainingList.get(leftCount).getObservation().getFeatureValue(attribute);
                    while (leftCount < trainingList.size()
                            && trainingList.get(leftCount).getObservation().getFeatureValue(attribute) == feature) {
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
                        double f1 = trainingElement1.getObservation().getFeatureValue(attribute);
                        double f2 = trainingElement2.getObservation().getFeatureValue(attribute);
                        if (f1 != f2 ||
                              f1 != trainingList.get(trainingList.size() - 1).getObservation().getFeatureValue(attribute)) {
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
            return features.getFeatureValue(testedFeature) <= cut;
        }

        public boolean isRight(FeatureVector features) {
            return features.getFeatureValue(testedFeature) > cut;
        }

    }
}
