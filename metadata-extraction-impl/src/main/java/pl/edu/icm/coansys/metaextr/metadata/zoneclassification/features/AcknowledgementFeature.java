package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

/** 
 * @author Pawel Szostek (p.szostek@icm.edu.pl) 
 */

public class AcknowledgementFeature implements
		FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "Acknowledgement";

    @Override
    public String getFeatureName() {
        return featureName;
    }

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