package pl.edu.icm.cermine.tools.classification.features;

import java.util.ArrayList;
import java.util.List;

/**
 * Feature vector builder (GoF factory pattern). The builder calculates feature
 * vectors for objects using a list of single feature calculators.
 * 
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 * 
 * @param <S>
 *            Type of objects for whom features' values can be calculated.
 * @param <T>
 *            Type of additional context objects that can be used for
 *            calculation.
 */
public class FeatureVectorBuilder<S, T> {

	private List<FeatureCalculator<S, T>> featureCalculators = new ArrayList<FeatureCalculator<S, T>>();

	public FeatureVector getFeatureVector(S object, T context) {
		FeatureVector featureVector = new FeatureVector();
		for (FeatureCalculator<S, T> fc : featureCalculators) {
			featureVector.addFeature(fc.getFeatureName(),
					fc.calculateFeatureValue(object, context));
		}
		return featureVector;
	}

	public List<String> getFeatureNames() {
		List<String> ret = new ArrayList<String>();
		for (FeatureCalculator<S, T> fc : featureCalculators) {
			ret.add(fc.getFeatureName());
		}
		return ret;
	}

	public int size() {
		return featureCalculators.size();
	}

	public List<FeatureCalculator<S, T>> getFeatureCalculators() {
		return featureCalculators;
	}

	public void setFeatureCalculators(
			List<FeatureCalculator<S, T>> featureCalculators) {
		this.featureCalculators = featureCalculators;
	}

}
