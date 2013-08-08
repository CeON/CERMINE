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
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class HMMProbabilityInfoFactory {

    private HMMProbabilityInfoFactory() {}
    
	/**
	 * 
	 * @param <S> a label type 
	 */
    public static <S extends Comparable<S>, X, Y> HMMProbabilityInfo<S> getFVHMMProbability(List<HMMTrainingSample<S>> trainingElements,
            FeatureVectorBuilder<X, Y> vectorBuilder) {
        return getFVHMMProbability(trainingElements, vectorBuilder, 0.0);
    }

    public static <S extends Comparable<S>, X, Y> HMMProbabilityInfo<S> getFVHMMProbability(List<HMMTrainingSample<S>> trainingElements,
            FeatureVectorBuilder<X,Y> vectorBuilder, double smoothing) {
        HMMProbabilityInfo<S> hmmProbabilities = new SimpleHMMProbabilityInfo<S>();

        HMMInitialProbability<S> initialProbability = new SimpleHMMInitialProbability<S>(trainingElements, smoothing);
        hmmProbabilities.setInitialProbability(initialProbability);

        HMMTransitionProbability<S> transitionProbability = new SimpleHMMTransitionProbability<S>(trainingElements, smoothing);
        hmmProbabilities.setTransitionProbability(transitionProbability);

        HMMEmissionProbability<S> emissionProbability = new DecisionTreeHMMEmissionProbability<S>(
                trainingElements, vectorBuilder.getFeatureNames(), smoothing);
        hmmProbabilities.setEmissionProbability(emissionProbability);

        return hmmProbabilities;
    }
}
