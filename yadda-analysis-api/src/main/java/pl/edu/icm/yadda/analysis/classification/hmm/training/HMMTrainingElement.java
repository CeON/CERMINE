package pl.edu.icm.yadda.analysis.classification.hmm.training;

/**
 * Hidden Markov Model's training element interface.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 * @param <S> Type of labels of objects.
 * @param <T> Type of observations.
 */
public interface HMMTrainingElement<S,T> {

    /**
     * Gets observation emitted by an element.
     *
     * @return Object's observation.
     */
    T getObservation();

    /**
     * Checks whether an element is first in the HMM's sequence.
     *
     * @return true if element is first, false otherwise
     */
    boolean isFirst();

    /**
     * Gets label of an element.
     *
     * @return The label.
     */
    S getLabel();

    /**
     * Gets the label of the following element, or null if the element is the
     * last in the HMM's sequence.
     *
     * @return The label of the following element.
     */
    S getNextLabel();

}
