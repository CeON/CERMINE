package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.features.AbstractFeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

public class IsAfterMetTitleFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		if(object.getPrev() == null || object.getPrev().getLabel() != BxZoneLabel.MET_TITLE)
			return 0.0;
		return 1.0;
	}

}
