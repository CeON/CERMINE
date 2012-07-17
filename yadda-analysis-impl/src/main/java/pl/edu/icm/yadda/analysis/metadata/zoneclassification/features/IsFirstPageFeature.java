package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import java.util.List;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

/** 
 * @author Pawel Szostek (p.szostek@icm.edu.pl) 
 */

public class IsFirstPageFeature implements FeatureCalculator<BxZone, BxPage> {
	private static String featureName = "IsFirstPage";

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		return page.getId().equals(new String("0")) == true ? 1.0 : 0.0;
	}
}
