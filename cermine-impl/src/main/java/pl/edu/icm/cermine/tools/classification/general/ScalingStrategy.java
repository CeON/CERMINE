package pl.edu.icm.cermine.tools.classification.general;

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

public interface ScalingStrategy {
	FeatureVector scaleFeatureVector(Double scaledLowerBound,
			Double scaledUpperBound, FeatureLimits[] limits, FeatureVector fv);
}
