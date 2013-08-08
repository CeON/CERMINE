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

package pl.edu.icm.cermine.tools.classification.hmm;

import java.util.Collection;
import java.util.List;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;

/**
 * Hidden Markov Models service interface. HMM service is able to perform
 * calculations related to Hidden Markov Models.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface HMMService {

    /**
     * Calculates the most probable states of a sequence of objects using
     * Viterbi algorithm.
     *
     * @param <S> A type of hidden labels
     * @param probabilityInfo HMM probability information.
     * @param states A collection of all possible states.
     * @param observations A sequence of messages emited by objects.
     * @return A sequence of the most probable object states, the order is the
     * same as in the message list.
     */
    <S> List<S> viterbiMostProbableStates(HMMProbabilityInfo<S> probabilityInfo, Collection<S> states,
                                            List<FeatureVector> observations);

}
