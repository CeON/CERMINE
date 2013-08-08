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
 * Hidden Markov Model's emission probability interface.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <S> a type of labels.
 */
public interface HMMEmissionProbability<S> {

    /**
     * Returns HMM's emission probability (the probability that an object with
     * a given label emits given message).
     *
     * @param observation An observation emitted by an object.
     * @return HMM emission probability.
     */
    double getProbability(S label, FeatureVector observation);
}
