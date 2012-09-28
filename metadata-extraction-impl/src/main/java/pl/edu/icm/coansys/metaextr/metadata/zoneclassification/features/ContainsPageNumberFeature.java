package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import java.util.regex.Pattern;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ContainsPageNumberFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "ContainsPageNumber";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        if (zone.getLines().size() > 1) {
            return 0;
        }
        if (Pattern.matches("^\\d+$|^Page\\s+.*$|^page\\s+.*$", zone.toText())) {
            return 1;
        }
        return 0;
    }

}
