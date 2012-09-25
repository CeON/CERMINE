package pl.edu.icm.coansys.metaextr.classification.features;

import java.util.Set;

/**
 * Feature vector interface.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface FeatureVector {

	public Double[] getFeatures();
    /**
     * Returns a single feature value.
     *
     * @param name The name of a single feature.
     * @return Feature value.
     */
    public double getFeature(String name);

    public void setFeature(String name, Double value) throws RuntimeException;
    /**
     * Adds a feature value to the vector.
     *
     * @param name Feature name.
     * @param featureValue Feature value.
     */
    public void addFeature(String name, double featureValue);

    /**
     * Dumps content of a feature vector to a string
     * @return string of feature values in a human readable form 
     */
    public String dump();

    /**
     * Return a set of used feature names.
     * 
     * @return feature names set
     */
    public Set<String> getFeatureNames();
    
    public Integer size();
    
    public FeatureVector clone();
}
