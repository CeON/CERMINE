package pl.edu.icm.yadda.analysis.hmm.features;

/**
 * Feature calculator interface. The calculator is able to calculate
 * a single feature's value.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 *
 * @param <S> Type of objects whose feature value can be calculated.
 * @param <T> Type of an additional context object that can be used
 * for calculation.
 */
public interface FeatureCalculator<S, T> {

    /**
     * Returns the name of the feature that can be calculated by the calculator.
     * Two different feature calculators of the same parameter types should
     * return different feature names.
     *
     * @return Feature name.
     */
    String getFeatureName();

    /**
     * Calculates the value of a single feature.
     *
     * @param object An object whose feature value will be calculated.
     * @param context An additional context object used for calculation.
     * @return Calculated feature value.
     */
    double calculateFeatureValue(S object, T context);

}
