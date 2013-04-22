package pl.edu.icm.cermine.tools.classification.features;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Feature vector builder (GoF factory pattern). The builder calculates
 * feature vectors for objects using a list of single feature calculators.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 *
 * @param <S> Type of objects for whom features' values can be calculated.
 * @param <T> Type of additional context objects that can be used
 * for calculation.
 */
public class FeatureVectorBuilder<S, T> {
    
    private Map<String, FeatureCalculator<S, T>> featureCalculators = new HashMap<String, FeatureCalculator<S, T>>();

    public void setFeatureCalculators(Collection<FeatureCalculator<S, T>> featureCalculators) {
        for (FeatureCalculator<S, T> featureCalculator : featureCalculators) {
            this.featureCalculators.put(featureCalculator.getFeatureName(), featureCalculator);
        }
    }

    public FeatureVector getFeatureVector(S object, T context) {
        FeatureVector featureVector = new FeatureVector();
        for (String name : featureCalculators.keySet()) {
            featureVector.addFeature(name, featureCalculators.get(name).calculateFeatureValue(object, context));
        }
        return featureVector;
    }

    public Set<String> getFeatureNames() {
        return featureCalculators.keySet();
    }
    
    public int size() {
    	return featureCalculators.size();
    }
    
}
