package pl.edu.icm.yadda.analysis.classification.tools;

import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;

public interface ScalingStrategy {
	public FeatureVector scaleFeatureVector(Double scaledLowerBound,
			Double scaledUpperBound, FeatureLimits[] limits, FeatureVector fv);
}
