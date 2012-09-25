package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class KeywordsFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "Keywords";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        String[] keywords = {"keywords", "key words"};

        for (String keyword : keywords) {
            if (zone.toText().toLowerCase().startsWith(keyword)) {
            	return 1.0;
            }
        }
        return 0.0;
    }

}
