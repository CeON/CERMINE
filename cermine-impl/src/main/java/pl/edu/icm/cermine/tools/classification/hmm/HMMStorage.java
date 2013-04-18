package pl.edu.icm.cermine.tools.classification.hmm;

import java.io.IOException;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMEmissionProbability;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMInitialProbability;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMTransitionProbability;

/**
 * Hidden Markov Models storage interface. The interface takes care of storing 
 * and fetching HMM probability information.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface HMMStorage {

    /**
     * Stores HMM initial probability.
     *
     * @param <S> A type of labels.
     * @param hmmId HMM id.
     * @param probability Probability object to store.
     * @throws IOException
     */
    <S> void storeInitialProbability(String hmmId, HMMInitialProbability<S> probability) throws IOException;

    /**
     * Stores HMM transition probability.
     *
     * @param <S> A type of labels.
     * @param hmmId HMM id.
     * @param probability Probability object to store.
     * @throws IOException
     */
    <S> void storeTransitionProbability(String hmmId, HMMTransitionProbability<S> probability) throws IOException;

    /**
     * Stores HMM emission probability.
     *
     * @param <S> A type of labels.
     * @param hmmId HMM id.
     * @param probability Probability object to store.
     * @throws IOException
     */
    <S> void storeEmissionProbability(String hmmId, HMMEmissionProbability<S> probability) throws IOException;

    /**
     * Fetches stored probability information object.
     *
     * @param <S> A type of labels.
     * @param hmmId HMM id.
     * @return Stored probability object.
     */
    <S> HMMProbabilityInfo<S> getProbabilityInfo(String hmmId) throws IOException;
}
