package pl.edu.icm.yadda.analysis.hmm.probability;

/**
 * Hidden Markov Model's emission probability interface.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <S> A type of labels.
 * @param <T> A type of observation.
 */
public interface HMMEmissionProbability<S,T> {

    /**
     * Returns HMM's emission probability (the probability that an object with
     * a given label emits given message).
     *
     * @param label A label of an object.
     * @param observation An observation emitted by an object.
     * @return HMM emission probability.
     */
    double getProbability(S label, T observation);
}
