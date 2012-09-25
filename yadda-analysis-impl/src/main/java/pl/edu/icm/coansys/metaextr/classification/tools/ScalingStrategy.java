package pl.edu.icm.coansys.metaextr.classification.tools;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureVector;

public interface ScalingStrategy {
	public FeatureVector scaleFeatureVector(Double scaledLowerBound,
			Double scaledUpperBound, FeatureLimits[] limits, FeatureVector fv);
}
