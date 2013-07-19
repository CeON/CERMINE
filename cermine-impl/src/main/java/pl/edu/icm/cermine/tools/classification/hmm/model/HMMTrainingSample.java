package pl.edu.icm.cermine.tools.classification.hmm.model;

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 * 
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class HMMTrainingSample<S> extends TrainingSample<S> {

    private S nextLabel;
    private boolean first;

    public HMMTrainingSample(FeatureVector observation, S label, boolean first) {
        super(observation, label);
        this.first = first;
    }

    public void setNextLabel(S nextLabel) {
        this.nextLabel = nextLabel;
    }

    public FeatureVector getObservation() {
        return getFeatureVector();
    }

    public boolean isFirst() {
        return first;
    }

    public S getNextLabel() {
        return nextLabel;
    }
    
    @Override
    public HMMTrainingSample<S> clone() throws CloneNotSupportedException {
        HMMTrainingSample<S> element = (HMMTrainingSample) super.clone();
        element.first = first;
        element.nextLabel = nextLabel;
        return element;
    }

}
