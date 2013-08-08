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

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 * Simple Hidden Markov Model probability information container class implementation.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMProbabilityInfo<S> implements HMMProbabilityInfo<S> {

    private HMMInitialProbability<S> initialProbability;
    private HMMTransitionProbability<S> transitionProbability;
    private HMMEmissionProbability<S> emissionProbability;

    public SimpleHMMProbabilityInfo() {
    }

    public SimpleHMMProbabilityInfo(HMMInitialProbability<S> initialProbability,
            HMMTransitionProbability<S> transitionProbability, HMMEmissionProbability<S> decisionTree) {
        this.initialProbability = initialProbability;
        this.transitionProbability = transitionProbability;
        this.emissionProbability = decisionTree;
    }

    @Override
    public void setInitialProbability(HMMInitialProbability<S> initialProbability) {
        this.initialProbability = initialProbability;
    }

    @Override
    public void setTransitionProbability(HMMTransitionProbability<S> transitionProbability) {
        this.transitionProbability = transitionProbability;
    }

    @Override
    public void setEmissionProbability(HMMEmissionProbability<S> emissionProbability) {
        this.emissionProbability = emissionProbability;
    }

    @Override
    public double getInitialProbability(S label) {
        return initialProbability.getProbability(label);
    }

    @Override
    public double getTransitionProbability(S startLabel, S endLabel) {
        return transitionProbability.getProbability(startLabel, endLabel);
    }

    @Override
    public double getEmissionProbability(S label, FeatureVector observation) {
        return emissionProbability.getProbability(label, observation);
    }

}
