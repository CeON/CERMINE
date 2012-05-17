package pl.edu.icm.yadda.analysis.zone.classification.features;

import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.hmm.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PageNumberFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "PageNumber";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        if (zone.getLines().size() > 1) {
            return 0;
        }
        int i = 1;
        for (BxZone z : page.getZones()) {
            if (z.equals(zone) && i > 3 && i < page.getZones().size() - 2) {
                return 0;
            }
            i++;
        }
        if (Pattern.matches("^\\d+$", zone.toText())
                || Pattern.matches("^Page.*$", zone.toText())
                || Pattern.matches("^page.*$", zone.toText())) {
            return 1;
        }
        return 0;
    }

}
