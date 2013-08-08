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
        return !isLeaf() && features.getValue(featureName) <= featureCut;
    }

    public boolean isClassifiedRight(FeatureVector features) {
        return !isLeaf() && features.getValue(featureName) > featureCut;
    }

    public int getLabelCount(T label) {
        return labelsProbability.getEventCount(label);
    }

}
