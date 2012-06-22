package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class LineHeightMaxMeanFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "LineHeightMeanRelative";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        double zoneMean = 0;
        for (BxLine line : zone.getLines()) {
            zoneMean += line.getBounds().getHeight();
        }
        zoneMean /= (double) zone.getLines().size();
        for (BxZone z : page.getZones()) {
            if (z.equals(zone)) {
                continue;
            }
            double pageMean = 0;
            for (BxLine line : z.getLines()) {
                pageMean += line.getBounds().getHeight();
            }
            pageMean /= z.getLines().size();
            if (pageMean > zoneMean + 1) {
                return 0;
            }
        }
        return 1;
    }

}
