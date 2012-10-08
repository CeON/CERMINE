package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/** 
 * @author Pawel Szostek (p.szostek@icm.edu.pl) 
 */

public class AcknowledgementFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        String[] keywords = {"acknowledge", "acknowledgement", "acknowledgment"};

        for (String keyword : keywords) {
            if (zone.toText().toLowerCase().contains(keyword)) {
            	return 1;
            }
        }
        return 0;
    }

}