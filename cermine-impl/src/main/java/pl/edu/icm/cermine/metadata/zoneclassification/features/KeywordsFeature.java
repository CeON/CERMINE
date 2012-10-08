package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class KeywordsFeature extends FeatureCalculator<BxZone, BxPage> {

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
