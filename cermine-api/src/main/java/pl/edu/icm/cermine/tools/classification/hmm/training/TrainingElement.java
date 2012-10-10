package pl.edu.icm.cermine.tools.classification.hmm.training;

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 * Training element interface for object classification.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 * @param <S> Type of labels of objects.
 */
public interface TrainingElement<S> extends Cloneable {

    /**
     * Gets observation emitted by an element.
     *
     * @return Object's observation.
     */
    FeatureVector getObservation();

    /**
     * Checks whether an element is first in the sequence.
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
    
    void setLabel(S label);
    

    /**
     * Gets the label of the following element, or null if the element is the
     * last in the sequence.
     *
     * @return The label of the following element.
     */
    S getNextLabel();
    
    TrainingElement<S> clone();

}
