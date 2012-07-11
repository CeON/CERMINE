package pl.edu.icm.yadda.analysis.classification.hmm.training;

import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;

/**
 * Simple HMM training element implementation.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class SimpleHMMTrainingElement<S> implements HMMTrainingElement<S> {

    private FeatureVector observation;
    private S label;
    private S nextLabel;
    private boolean first;


    public SimpleHMMTrainingElement(FeatureVector observation, S label, boolean first) {
        this.observation = observation;
        this.label = label;
        this.first = first;
    }

    public void setNextLabel(S nextLabel) {
        this.nextLabel = nextLabel;
    }

    @Override
    public FeatureVector getObservation() {
        return observation;
    }

    @Override
    public boolean isFirst() {
        return first;
    }

    @Override
    public S getLabel() {
        return label;
    }

    @Override
    public S getNextLabel() {
        return nextLabel;
    }

}
