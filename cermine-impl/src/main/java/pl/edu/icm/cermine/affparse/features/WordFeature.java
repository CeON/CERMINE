package pl.edu.icm.cermine.affparse.features;

import java.util.List;

public class WordFeature extends LocalFeature {

	private List<LocalFeature> blockingFeatures;
	private boolean toLowerCase;
	// By convention, see: https://dl.dropboxusercontent.com/u/55174954/grmm.htm
	private static final String PREFIX = "W=";
	
	public WordFeature(List<LocalFeature> blockingFeatures, boolean toLowerCase) {
		this.blockingFeatures = blockingFeatures;
		this.toLowerCase = toLowerCase;
	}
	
	@Override
	public String computeFeature(String text) {
		for (LocalFeature feature : blockingFeatures) {
			if (feature.computeFeature(text) != null) {
				return null;
			}
		}
		if (toLowerCase) {
			return PREFIX + text.toLowerCase(); 
		} else {
			return PREFIX + text;
		}
	}
}
