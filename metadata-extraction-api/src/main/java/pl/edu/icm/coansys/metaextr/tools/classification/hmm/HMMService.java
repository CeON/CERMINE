package pl.edu.icm.coansys.metaextr.tools.classification.hmm;

import java.util.Collection;
import java.util.List;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMProbabilityInfo;

/**
 * Hidden Markov Models service interface. HMM service is able to perform
 * calculations related to Hidden Markov Models.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface HMMService {

    /**
     * Calculates the most probable states of a sequence of objects using
     * Viterbi algorithm.
     *
     * @param <S> A type of hidden labels
     * @param probabilityInfo HMM probability information.
     * @param states A collection of all possible states.
     * @param observations A sequence of messages emited by objects.
     * @return A sequence of the most probable object states, the order is the
     * same as in the message list.
     */
    <S> List<S> viterbiMostProbableStates(HMMProbabilityInfo<S> probabilityInfo, Collection<S> states,
                                            List<FeatureVector> observations);

}
