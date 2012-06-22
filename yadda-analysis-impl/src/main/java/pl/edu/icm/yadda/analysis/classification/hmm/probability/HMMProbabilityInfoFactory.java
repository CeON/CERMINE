package pl.edu.icm.yadda.analysis.classification.hmm.probability;

import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HMMProbabilityInfoFactory {

    public static <S> HMMProbabilityInfo<S,FeatureVector> getFVHMMProbability(HMMTrainingElement<S,FeatureVector>[] trainingElements,
            FeatureVectorBuilder vectorBuilder) throws Exception {
        return getFVHMMProbability(trainingElements, vectorBuilder, 0.0);
    }

    public static <S> HMMProbabilityInfo<S,FeatureVector> getFVHMMProbability(HMMTrainingElement<S,FeatureVector>[] trainingElements,
            FeatureVectorBuilder vectorBuilder, double smoothing) throws Exception {
        HMMProbabilityInfo<S,FeatureVector> hmmProbabilities = new SimpleHMMProbabilityInfo<S,FeatureVector>();

        HMMInitialProbability initialProbability = new SimpleHMMInitialProbability<S>(trainingElements, smoothing);
        hmmProbabilities.setInitialProbability(initialProbability);

        HMMTransitionProbability transitionProbability = new SimpleHMMTransitionProbability<S>(trainingElements, smoothing);
        hmmProbabilities.setTransitionProbability(transitionProbability);

        HMMEmissionProbability emissionProbability = new DecisionTreeHMMEmissionProbability(
                trainingElements, vectorBuilder.getFeatureNames(), smoothing);
        hmmProbabilities.setEmissionProbability(emissionProbability);

        return hmmProbabilities;
    }
}
