package pl.edu.icm.cermine.tools.classification.features;

import java.util.Collection;
import java.util.Set;

/**
 * Feature vector builder (GoF factory pattern). The builder calculates
 * feature vectors for objects using a list of single feature calculators.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 *
 * @param <X> Type of objects for whom features' values can be calculated.
 * @param <Y> Type of additional context objects that can be used
 * for calculation.
 */
public interface FeatureVectorBuilder<X, Y> {

    /**
     * Sets feature calculators used for building feature vectors.
     *
     * @param featureCalculators A collection of feature calculators.
     */
    void setFeatureCalculators(Collection<FeatureCalculator<X, Y>> featureCalculators);

    /**
     * Returns calculated feature vector.
     *
     * @param object An object, whose feature vector is to be calculated.
     * @param context Context object
     * @return Calculated feature vector.
     */
    FeatureVector getFeatureVector(X object, Y context);

    /**
     * Returns the names of features that are part of calculated feature vector.
     *
     * @return The set of feature names.
     */
    Set<String> getFeatureNames();

    int size();

}
