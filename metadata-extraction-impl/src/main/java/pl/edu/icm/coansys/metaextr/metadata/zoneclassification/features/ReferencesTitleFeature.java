package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ReferencesTitleFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "ReferencesTitle";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        String[] keywords = {"referen", "biblio"};

        for (String keyword : keywords) {
            if (zone.toText().toLowerCase().startsWith(keyword)) {
                return 1;
            }
        }
        return 0;
    }

}
