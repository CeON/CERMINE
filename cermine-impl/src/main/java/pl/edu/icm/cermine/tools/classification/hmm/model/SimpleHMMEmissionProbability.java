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
import java.util.Map;
import pl.edu.icm.cermine.structure.tools.ProbabilityDistribution;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 * Simple Hidden Markov Model emission probability implementation.
 *
 * @param <S> a label type
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMEmissionProbability<S> implements HMMEmissionProbability<S> {

    private Map<S, ProbabilityDistribution<FeatureVector>> probability;

    private double zeroProbabilityValue;

    public SimpleHMMEmissionProbability(HMMTrainingSample<S>[] trainingElements) {
        this(trainingElements, 0.0);
    }

    public SimpleHMMEmissionProbability(HMMTrainingSample<S>[] trainingElements, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        probability = new HashMap<S, ProbabilityDistribution<FeatureVector>>();
        for (HMMTrainingSample<S> element : trainingElements) {

            if (!probability.containsKey(element.getLabel())) {
                probability.put(element.getLabel(), new ProbabilityDistribution<FeatureVector>());
            }
            probability.get(element.getLabel()).addEvent(element.getObservation());

        }
    }

    @Override
    public double getProbability(S label, FeatureVector observation) {
        double prob = zeroProbabilityValue;
        if (probability.containsKey(label)) {
            prob = probability.get(label).getProbability(observation);
            if (prob == 0) {
                prob = zeroProbabilityValue;
            }
        }
        return prob;
    }

}
