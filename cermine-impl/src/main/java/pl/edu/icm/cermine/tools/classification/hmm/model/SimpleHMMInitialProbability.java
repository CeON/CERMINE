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

import java.util.List;
import pl.edu.icm.cermine.structure.tools.ProbabilityDistribution;

/**
 * Simple Hidden Markov Model initial probability implementation.
 *
 * @param <S> A label type 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMInitialProbability<S> implements HMMInitialProbability<S> {

    private ProbabilityDistribution<S> probability;

    private double zeroProbabilityValue;

    public SimpleHMMInitialProbability(List<HMMTrainingSample<S>> trainingElements) {
        this(trainingElements, 0.0);
    }

    public SimpleHMMInitialProbability(List<HMMTrainingSample<S>> trainingElements, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        probability = new ProbabilityDistribution<S>();
        for (HMMTrainingSample<S> element : trainingElements) {
            if (element.isFirst()) {
                probability.addEvent(element.getLabel());
            }
        }
    }

    @Override
    public double getProbability(S label) {
        return (probability.getProbability(label) == 0) ? zeroProbabilityValue : probability.getProbability(label);
    }

}
