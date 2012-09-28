package pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability;

import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMTransitionProbability;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMInitialProbability;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMEmissionProbability;
import java.util.List;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.TrainingElement;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HMMProbabilityInfoFactory {

	/**
	 * 
	 * @param <S> a label type 
	 */
    public static <S extends Comparable<S>, X, Y> HMMProbabilityInfo<S> getFVHMMProbability(List<TrainingElement<S>> trainingElements,
            FeatureVectorBuilder<X, Y> vectorBuilder) throws Exception {
        return getFVHMMProbability(trainingElements, vectorBuilder, 0.0);
    }

    public static <S extends Comparable<S>, X, Y> HMMProbabilityInfo<S> getFVHMMProbability(List<TrainingElement<S>> trainingElements,
            FeatureVectorBuilder<X,Y> vectorBuilder, double smoothing) throws Exception {
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
