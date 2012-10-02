package pl.edu.icm.coansys.metaextr.tools.classification.hmm.model;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;

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
