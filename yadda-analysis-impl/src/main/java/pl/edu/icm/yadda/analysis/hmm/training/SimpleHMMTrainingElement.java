package pl.edu.icm.yadda.analysis.hmm.training;

/**
 * Simple HMM training element implementation.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class SimpleHMMTrainingElement<S,T> implements HMMTrainingElement<S,T> {

    private T observation;
    private S label;
    private S nextLabel;
    private boolean first;


    public SimpleHMMTrainingElement(T observation, S label, boolean first) {
        this.observation = observation;
        this.label = label;
        this.first = first;
    }

    public void setNextLabel(S nextLabel) {
        this.nextLabel = nextLabel;
    }

    @Override
    public T getObservation() {
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
