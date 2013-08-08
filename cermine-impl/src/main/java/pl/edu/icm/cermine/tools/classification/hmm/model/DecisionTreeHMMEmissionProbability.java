/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.tools.classification.hmm.model;

import java.util.HashSet;
import java.util.List;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 * Hidden Markov Model emission probability implementation based on a decision
 * tree.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DecisionTreeHMMEmissionProbability<S extends Comparable<S>> implements HMMEmissionProbability<S> {

    private DecisionTree<S> decisionTree;

    private double zeroProbabilityValue;

    public DecisionTreeHMMEmissionProbability(List<HMMTrainingSample<S>> trainingElements,
                                              List<String> featureNames) {
        this(trainingElements, featureNames, 0.0);
    }

    public DecisionTreeHMMEmissionProbability(List<HMMTrainingSample<S>> trainingElements, List<String> featureNames, int decisionTreeExpand) {
        this(trainingElements, featureNames, decisionTreeExpand, 0.0);
    }

    public DecisionTreeHMMEmissionProbability(List<HMMTrainingSample<S>> trainingElements,
                                              List<String> featureNames, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        decisionTree = DecisionTreeBuilder.buildDecisionTree(new HashSet<HMMTrainingSample<S>>(trainingElements), featureNames);
    }

    public DecisionTreeHMMEmissionProbability(List<HMMTrainingSample<S>> trainingElements,
                                              List<String> featureNames, int decisionTreeExpand, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        decisionTree = DecisionTreeBuilder.buildDecisionTree(new HashSet<HMMTrainingSample<S>>(trainingElements), featureNames, decisionTreeExpand);
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
