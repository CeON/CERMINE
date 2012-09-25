package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class LicenseFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "License";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        String[] keywords = {"terms", "distributed", "reproduction", "open", "commons",
            "license", "©", "creative", "copyright", "cited", "distribution", "access"};

        int count = 0;
        for (String keyword : keywords) {
            if (zone.toText().toLowerCase().contains(keyword)) {
                count++;
            }
        }

        return count;
    }

}
