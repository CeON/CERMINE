package pl.edu.icm.cermine.tools.classification.general;

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

public class LinearScaling implements ScalingStrategy {

	@Override
	public FeatureVector scaleFeatureVector(Double scaledLowerBound,
			Double scaledUpperBound, FeatureLimits[] limits, FeatureVector fv) {
		final double EPS = 0.00001;
		FeatureVector newVector = new FeatureVector();
		int featureIdx = 0;
		for(String name: fv.getFeatureNames()) {
			//scaling function: y = a*x+b
			// featureLower = a*v_min + b
			// featureUpper = a*v_max + b
			if(Math.abs(limits[featureIdx].getMax()-limits[featureIdx].getMin()) < EPS) {
				newVector.addFeature(name, 1.0);
			} else {
				Double featureValue = fv.getFeature(name);
				Double a = (scaledUpperBound-scaledLowerBound)/(limits[featureIdx].getMax()-limits[featureIdx].getMin());
				Double b = scaledLowerBound-a*limits[featureIdx].getMin();
				
				featureValue = a*featureValue+b; 
				//assert featureValue != Double.NaN;
				if (featureValue.isNaN()) {
					throw new RuntimeException("Feature value is set to NaN: "+name);
				}
				newVector.addFeature(name, featureValue);
			}
			++featureIdx;
		}
		return newVector;
	}
}
