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
 * Hidden Markov Model's probability information.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <S> A type of labels.
 */
public interface HMMProbabilityInfo<S> {

    /**
     * Sets HMM's initial probability.
     *
     * @param initialProbability HMM's initial probability
     */
    void setInitialProbability(HMMInitialProbability<S> initialProbability);

    /**
     * Sets HMM's transition probability.
     *
     * @param transitionProbability HMM's transition probability
     */
    void setTransitionProbability(HMMTransitionProbability<S> transitionProbability);

    /**
     * Sets HMM's emission probability.
     *
     * @param emissionProbability HMM's emission probability
     */
    void setEmissionProbability(HMMEmissionProbability<S> emissionProbability);

    /**
     * Gets HMM's initial probability (the probability that the first object in
     * the sequence has given label).
     *
     * @param label Label of an object.
     * @return HMM's initial probability.
     */
    double getInitialProbability(S label);

    /**
     * Gets HMM's transition probability (the probability that an object with
     * the first label is followed by an object with the second label).
     *
     * @param startLabel Label of the first object.
     * @param endLabel Label of the second object.
     * @return HMM's transition probability.
     */
    double getTransitionProbability(S startLabel, S endLabel);

    /**
     * Gets HMM's emission probability (the probability that an object with
     * given label emits given observation).
     *
     * @param label Label of an object.
     * @param observation Emitted message.
     * @return HMM's emission probability.
     */
    double getEmissionProbability(S label, FeatureVector observation);
}
