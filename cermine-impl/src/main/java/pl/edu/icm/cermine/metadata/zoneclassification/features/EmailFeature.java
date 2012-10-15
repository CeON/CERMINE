package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;

public class EmailFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		String text = object.toText();
		text = text.toLowerCase();
		if(text.matches(".*[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4}).*")) {
			return 1.0;
		} else {
			return 0.0;
        }
	}

}
