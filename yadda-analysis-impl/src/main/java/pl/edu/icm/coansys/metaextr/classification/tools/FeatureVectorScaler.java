package pl.edu.icm.coansys.metaextr.classification.tools;

import java.util.List;
import java.util.Set;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

public class FeatureVectorScaler {
	protected FeatureLimits[] limits;
	protected Double scaledLowerBound;
	protected Double scaledUpperBound;
	protected ScalingStrategy strategy;
	
	public FeatureVectorScaler(Integer size, Double lowerBound, Double upperBound) {
		this.scaledLowerBound = lowerBound;
		this.scaledUpperBound = upperBound;
		limits = new FeatureLimits[size];
		//set default limits to: max = -inf, min = +inf
		for(int idx=0; idx<size; ++idx) {
			limits[idx] = new FeatureLimits(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		}
	}
	
	public void setStrategy(ScalingStrategy strategy) {
		this.strategy = strategy;
	}
	
	public FeatureVector scaleFeatureVector(FeatureVector fv) {
		return strategy.scaleFeatureVector(scaledLowerBound, scaledUpperBound, limits, fv);
	}
	
	public void setFeatureLimits(List<TrainingElement<BxZoneLabel>> trainingElements) 	{
		for(TrainingElement<BxZoneLabel> trainingElem: trainingElements) {
			FeatureVector fv = trainingElem.getObservation();
			Set<String> names = fv.getFeatureNames();
			
			int featureIdx = 0;
			for(String name: names) {
				double val = fv.getFeature(name);
				if(val > limits[featureIdx].max) {
					limits[featureIdx].setMax(val);
				}
				if(val < limits[featureIdx].min){
					limits[featureIdx].setMin(val);
				}
				++featureIdx;
			}
		}
		Integer idx=0;
		for(FeatureLimits limit: limits) {
			assert limit.getMin() != Double.MAX_VALUE;
			assert limit.getMax() != Double.MIN_VALUE;
		}
	}
	
	public FeatureLimits[] getLimits() {
		return limits;
	}
}
