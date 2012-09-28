package pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;

/**
 * Hidden Markov Model's probability information.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <S> A type of labels.
 */
public interface HMMProbabilityInfo<S> {

    /**
     * Sets HMM's initial probability.
     *
     * @param initialProbability HMM's initial probability
     */
    void setInitialProbability(HMMInitialProbability<S> initialProbability);

    /**
     * Sets HMM's transition probability.
     *
     * @param transitionProbability HMM's transition probability
     */
    void setTransitionProbability(HMMTransitionProbability<S> transitionProbability);

    /**
     * Sets HMM's emission probability.
     *
     * @param emissionProbability HMM's emission probability
     */
    void setEmissionProbability(HMMEmissionProbability<S> emissionProbability);

    /**
     * Gets HMM's initial probability (the probability that the first object in
     * the sequence has given label).
     *
     * @param label Label of an object.
     * @return HMM's initial probability.
     */
    double getInitialProbability(S label);

    /**
     * Gets HMM's transition probability (the probability that an object with
     * the first label is followed by an object with the second label).
     *
     * @param startLabel Label of the first object.
     * @param endLabel Label of the second object.
     * @return HMM's transition probability.
     */
    double getTransitionProbability(S startLabel, S endLabel);

    /**
     * Gets HMM's emission probability (the probability that an object with
     * given label emits given observation).
     *
     * @param label Label of an object.
     * @param observation Emitted message.
     * @return HMM's emission probability.
     */
    double getEmissionProbability(S label, FeatureVector observation);
}
