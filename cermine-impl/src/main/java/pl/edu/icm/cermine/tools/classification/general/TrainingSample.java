package pl.edu.icm.cermine.tools.classification.general;

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 *
 * @author Dominika Tkaczyk
 */
public class TrainingSample<S> implements Cloneable {
    
    private FeatureVector features;
    private S label;
  
    public TrainingSample(FeatureVector features, S label) {
        this.features = features;
        this.label = label;
    }
    
    public FeatureVector getFeatureVector() {
        return features;
    }

    public void setFeatures(FeatureVector features) {
        this.features = features;
    }

    public S getLabel() {
        return label;
    }

    public void setLabel(S label) {
        this.label = label;
    }

    @Override
    public TrainingSample<S> clone() throws CloneNotSupportedException {
        TrainingSample<S> element = (TrainingSample) super.clone();
        element.label = label;
        element.features = features.clone();
        return element;
    }
}
