package pl.edu.icm.yadda.analysis.hmm.probability;

/**
 * Hidden Markov Model's initial probability interface.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <S> A type of labels.
 */
public interface HMMInitialProbability<S> {

    /**
     * Returns HMM's initial probability (the probability that the first object
     * in the sequence has given label).
     *
     * @param label A label of an object
     * @return HMM initial probability
     */
    double getProbability(S label);

}
