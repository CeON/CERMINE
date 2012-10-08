package pl.edu.icm.cermine.metadata.zoneclassification.features;

import java.util.regex.Pattern;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ContainsPageNumberFeature extends FeatureCalculator<BxZone, BxPage> {

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
