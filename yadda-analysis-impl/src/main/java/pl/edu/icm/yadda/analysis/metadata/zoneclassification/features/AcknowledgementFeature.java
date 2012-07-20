package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

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