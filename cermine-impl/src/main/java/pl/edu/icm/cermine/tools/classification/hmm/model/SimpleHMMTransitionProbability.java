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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.structure.tools.ProbabilityDistribution;

/**
 * Simple Hidden Markov Model transition probability implementation.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMTransitionProbability<S> implements HMMTransitionProbability<S> {

    private Map<S, ProbabilityDistribution<S>> probability;

    private double zeroProbabilityValue;

    public SimpleHMMTransitionProbability(List<HMMTrainingSample<S>> trainingElements) {
        this(trainingElements, 0.0);
    }

    public SimpleHMMTransitionProbability(List<HMMTrainingSample<S>> trainingElements, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        probability = new HashMap<S, ProbabilityDistribution<S>>();
        for (HMMTrainingSample<S> element : trainingElements) {
            if (element.getNextLabel() != null) {
                if (!probability.containsKey(element.getLabel())) {
                    probability.put(element.getLabel(), new ProbabilityDistribution<S>());
                }
                probability.get(element.getLabel()).addEvent(element.getNextLabel());
            }
        }
    }

    @Override
    public double getProbability(S startLabel, S endLabel) {
        double prob = zeroProbabilityValue;
        if (probability.containsKey(startLabel)) {
            prob = probability.get(startLabel).getProbability(endLabel);
            if (prob == 0) {
                prob = zeroProbabilityValue;
            }
        }
        return prob;
    }

}
