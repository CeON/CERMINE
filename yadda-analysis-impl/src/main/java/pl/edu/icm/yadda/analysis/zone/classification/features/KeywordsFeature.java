package pl.edu.icm.yadda.analysis.zone.classification.features;

import pl.edu.icm.yadda.analysis.hmm.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

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

        int count = 0;
        for (String keyword : keywords) {
            if (zone.toText().toLowerCase().startsWith(keyword)) {
                count++;
            }
        }

        return count;
    }

}
