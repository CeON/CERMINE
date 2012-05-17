package pl.edu.icm.yadda.analysis.hmm.features;

/**
 * Feature vector interface.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface FeatureVector {

    /**
     * Returns a single feature value.
     *
     * @param name The name of a single feature.
     * @return Feature value.
     */
    double getFeature(String name);

    /**
     * Adds a feature value to the vector.
     *
     * @param name Feature name.
     * @param featureValue Feature value.
     */
    void addFeature(String name, double featureValue);

}
