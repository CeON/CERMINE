package pl.edu.icm.cermine.tools.classification.general;

public class FeatureLimits 
{
	public FeatureLimits(Double minValue, Double maxValue) {
		min = minValue;
		max = maxValue;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	Double min;
	Double max;
}