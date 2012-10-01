package pl.edu.icm.coansys.metaextr.tools.classification.general;

import java.util.List;
import java.util.Set;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;

public class LinearScaling implements ScalingStrategy {

	@Override
	public FeatureVector scaleFeatureVector(Double scaledLowerBound,
			Double scaledUpperBound, FeatureLimits[] limits, FeatureVector fv) {
		final double EPS = 0.00001;
		FeatureVector newVector = new SimpleFeatureVector();
		
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
				assert featureValue != Double.NaN;
				newVector.addFeature(name, featureValue);
			}
			++featureIdx;
		}
		return newVector;
	}
}
