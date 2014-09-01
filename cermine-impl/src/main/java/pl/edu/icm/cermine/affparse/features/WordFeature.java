package pl.edu.icm.cermine.affparse.features;

import java.util.List;

public class WordFeature extends LocalFeature {

	private List<LocalFeature> blockingFeatures;
	
	public WordFeature(List<LocalFeature> blockingFeatures) {
		this.blockingFeatures = blockingFeatures;
	}
	
	@Override
	public String computeFeature(String text) {
		for (LocalFeature feature : blockingFeatures) {
			if (feature.computeFeature(text) != null) {
				return null;
			}
		}
		return "W=" + text.toLowerCase();
	}
}
