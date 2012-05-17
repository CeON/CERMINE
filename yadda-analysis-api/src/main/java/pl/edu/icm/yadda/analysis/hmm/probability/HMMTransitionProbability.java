package pl.edu.icm.yadda.analysis.hmm.probability;

/**
 * Hidden Markov Model's transition probability interface.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <S> A type of labels.
 */
public interface HMMTransitionProbability<S> {

    /**
     * Gets HMM's transition probability (the probability that an object with
     * the first label is followed by an object with the second label).
     *
     * @param startLabel Label of the first object.
     * @param endLabel Label of the second object.
     * @return HMM's transition probability.
     */
    double getProbability(S startLabel, S endLabel);
}
