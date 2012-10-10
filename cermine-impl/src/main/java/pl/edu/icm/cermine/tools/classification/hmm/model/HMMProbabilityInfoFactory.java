package pl.edu.icm.cermine.tools.classification.hmm.model;

import java.util.List;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HMMProbabilityInfoFactory {

    private HMMProbabilityInfoFactory() {}
    
	/**
	 * 
	 * @param <S> a label type 
	 */
    public static <S extends Comparable<S>, X, Y> HMMProbabilityInfo<S> getFVHMMProbability(List<TrainingElement<S>> trainingElements,
            FeatureVectorBuilder<X, Y> vectorBuilder) {
        return getFVHMMProbability(trainingElements, vectorBuilder, 0.0);
    }

    public static <S extends Comparable<S>, X, Y> HMMProbabilityInfo<S> getFVHMMProbability(List<TrainingElement<S>> trainingElements,
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
