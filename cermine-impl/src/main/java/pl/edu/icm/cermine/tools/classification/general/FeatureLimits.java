package pl.edu.icm.cermine.tools.classification.general;

public class FeatureLimits 
{
	public FeatureLimits(Double minValue, Double maxValue) {
		min = minValue;
		max = maxValue;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	double min;
	double max;
}