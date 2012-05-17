package pl.edu.icm.yadda.analysis.hmm.probability.decisiontree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pl.edu.icm.yadda.analysis.hmm.features.FeatureVector;
import pl.edu.icm.yadda.analysis.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.textr.tools.ProbabilityDistribution;

/**
 * Builds a decision tree using a collection of training elements.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class DecisionTreeBuilder {

    /* This field causes the expansion of tree nodes to be stopped, when there 
     * are less that stopExpanding training elements arriving to the node.
     */
    private static int stopExpanding = 20;

    /**
     * Builds a decision tree from a training set.
     *
     * @param <T> A type of element decisions (labels).
     * @param trainingSet A set of training elements.
     * @param attributes A set of feature names that can be used.
     * @return Root of the built decision tree.
     */
    public static <T extends Comparable> DecisionTree<T> buildDecisionTree(
            Set<HMMTrainingElement<T,FeatureVector>> trainingSet, Set<String> attributes) {
        return DecisionTreeBuilder.buildDecisionTree(trainingSet, attributes, stopExpanding);
    }

    /**
     * Builds a decision tree from a training set with a given stopExpanding
     * parameter.
     *
     * @param <T> A type of element decisions (labels).
     * @param trainingSet A set of training elements.
     * @param attributes A set of feature names that can be used.
     * @param stopExpanding stopExpanding parameter.
     * @return Root of the built decision tree.
     */
    public static <T extends Comparable> SimpleDecisionTree<T> buildDecisionTree(
            Set<HMMTrainingElement<T,FeatureVector>> trainingSet, Set<String> attributes, int stopExpanding) {
        return constructNode(trainingSet, attributes, stopExpanding);
    }

    /**
     * Constructs a decision tree node from a training set.
     *
     * @param <T> A type of element decisions (labels).
     * @param trainingSet A set of training elements.
     * @param attributes A set of attributes (feature vector indexes) thet can 
     * stil be used in decisions.
     * @param stopExpanding stopExpanding parameter.
     * @return Constructed node.
     */
    private static <T extends Comparable> SimpleDecisionTree<T> constructNode(
            Set<HMMTrainingElement<T, FeatureVector>> trainingSet, Set<String> attributes, int stopExpanding) {
        if (trainingSet.isEmpty()) {
            return null;
        }

        ProbabilityDistribution<T> probDistribution = new ProbabilityDistribution<T>();
        for (HMMTrainingElement<T,FeatureVector> element : trainingSet) {
            probDistribution.addEvent(element.getLabel());
        }
        if (probDistribution.getEvents().size() == 1 || attributes.isEmpty() || trainingSet.size() < stopExpanding) {
            return new SimpleDecisionTree<T>(probDistribution);
        }

        NodeDecision decision = chooseDecision(trainingSet, attributes);
        if (decision == null) {
            return new SimpleDecisionTree<T>(probDistribution);
        }

        Set<String> newAttributes = new HashSet<String>(attributes);
        newAttributes.remove(decision.testedFeature);

        Set<HMMTrainingElement<T,FeatureVector>> leftElements = new HashSet<HMMTrainingElement<T,FeatureVector>>();
        Set<HMMTrainingElement<T,FeatureVector>> rightElements = new HashSet<HMMTrainingElement<T,FeatureVector>>();

        for (HMMTrainingElement<T,FeatureVector> element : trainingSet) {
            if (decision.isLeft(element.getObservation())) {
                leftElements.add(element);
            } else {
                rightElements.add(element);
            }
        }

        SimpleDecisionTree<T> leftNode = constructNode(leftElements, newAttributes, stopExpanding);
        SimpleDecisionTree<T> rightNode = constructNode(rightElements, newAttributes, stopExpanding);

        return new SimpleDecisionTree(probDistribution, leftNode, rightNode, decision.testedFeature, decision.cut);
    }

    /**
     * Chooses the best decision (feature vector index and cut) for given
     * training set.
     *
     * @param <T> A type of element decisions (labels).
     * @param trainingSet A set of training elements.
     * @param attributes A set of attributes (feature vector indexes) thet can
     * stil be use in decisions.
     * @return The best decision.
     */
    private static <T extends Comparable> NodeDecision chooseDecision(
            Set<HMMTrainingElement<T,FeatureVector>> trainingSet, Set<String> attributes) {
        String bestAttribute = null;
        double bestCut = -1;
        double bestEntropyGain = 0;

        List<HMMTrainingElement<T,FeatureVector>> trainingList =
                new ArrayList<HMMTrainingElement<T,FeatureVector>>(trainingSet);

        for (String attribute : attributes) {
            final String sortAttribute = attribute;
            Collections.sort(trainingList, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    HMMTrainingElement<T,FeatureVector> te1 = (HMMTrainingElement<T,FeatureVector>) t;
                    HMMTrainingElement<T,FeatureVector> te2 = (HMMTrainingElement<T,FeatureVector>) t1;
                    int ret = Double.compare(te1.getObservation().getFeature(sortAttribute),
                            te2.getObservation().getFeature(sortAttribute));
                    if (ret == 0) {
                        ret = te1.getLabel().compareTo(te2.getLabel());
                    }
                    return ret;
                }
            });

            ProbabilityDistribution<T> leftLabelsProb = new ProbabilityDistribution<T>();
            ProbabilityDistribution<T> rightLabelsProb = new ProbabilityDistribution<T>();
            for (HMMTrainingElement<T,FeatureVector> element : trainingList) {
                rightLabelsProb.addEvent(element.getLabel());
            }

            int leftCount = 0;
            for (int i = 0; i < trainingList.size() - 1; i++) {

                HMMTrainingElement<T,FeatureVector> trainingElement1 = trainingList.get(i);
                HMMTrainingElement<T,FeatureVector> trainingElement2 = trainingList.get(i + 1);
                T label1 = trainingElement1.getLabel();
                T label2 = trainingElement2.getLabel();

                if (leftCount <= i) {
                    double feature = trainingList.get(leftCount).getObservation().getFeature(attribute);
                    while (leftCount < trainingList.size()
                            && trainingList.get(leftCount).getObservation().getFeature(attribute) == feature) {
                        leftLabelsProb.addEvent(trainingList.get(leftCount).getLabel());
                        rightLabelsProb.removeEvent(trainingList.get(leftCount).getLabel());
                        leftCount++;
                    }
                }

                if (label1 != label2) {
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

        String testedFeature;
        double cut;

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
