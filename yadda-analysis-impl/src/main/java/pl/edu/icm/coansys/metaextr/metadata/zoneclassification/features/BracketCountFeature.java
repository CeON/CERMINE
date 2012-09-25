package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;

public class BracketCountFeature implements FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "BracketCount";
	
	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		int bracketCount = 0;
		for(char c: zone.toText().toCharArray())
			if(c == '[' || c == ']')
				++bracketCount;
		return new Double(bracketCount); 
	}
}
