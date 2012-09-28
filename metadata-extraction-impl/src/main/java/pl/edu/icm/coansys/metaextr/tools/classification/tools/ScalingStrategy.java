package pl.edu.icm.coansys.metaextr.tools.classification.tools;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;

public interface ScalingStrategy {
	public FeatureVector scaleFeatureVector(Double scaledLowerBound,
			Double scaledUpperBound, FeatureLimits[] limits, FeatureVector fv);
}
