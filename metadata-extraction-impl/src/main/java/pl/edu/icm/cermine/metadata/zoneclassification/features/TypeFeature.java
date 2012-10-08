package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class TypeFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "Type";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        String[] keywords = {"research article", "review article", "editorial", "review", "debate", "case report",
                             "research", "original research", "methodology", "clinical study", "commentary", "article",
                             "hypothesis"};

        int count = 0;
        for (String keyword : keywords) {
            if (zone.toText().toLowerCase().equals(keyword)) {
                count++;
            }
        }

        return count;
    }

}
