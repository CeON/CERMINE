package pl.edu.icm.cermine.tools.classification.features;

import java.util.Set;

/**
 * Feature vector interface.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface FeatureVector {

	Double[] getFeatures();
    /**
     * Returns a single feature value.
     *
     * @param name The name of a single feature.
     * @return Feature value.
     */
    double getFeature(String name);

    void setFeature(String name, Double value);
    /**
     * Adds a feature value to the vector.
     *
     * @param name Feature name.
     * @param featureValue Feature value.
     */
    void addFeature(String name, double featureValue);

    /**
     * Dumps content of a feature vector to a string
     * @return string of feature values in a human readable form 
     */
    String dump();

    /**
     * Return a set of used feature names.
     * 
     * @return feature names set
     */
    Set<String> getFeatureNames();
    
    Integer size();
    
    FeatureVector clone();
}
