package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

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
