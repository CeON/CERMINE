package pl.edu.icm.cermine.affparse.features;

import java.util.List;

public class WordFeature extends Feature {

	private List<Feature> blockingFeatures;
	
	public WordFeature(List<Feature> blockingFeatures) {
		this.blockingFeatures = blockingFeatures;
	}
	
	@Override
	public String computeFeature(String text) {
		for (Feature feature : blockingFeatures) {
			if (feature.computeFeature(text) != null) {
				return null;
			}
		}
		return "W=" + text.toLowerCase();
	}
}
