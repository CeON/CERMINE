package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class LineXPositionDiffFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "LineXPositionDiff";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        double min = zone.getBounds().getX() + zone.getBounds().getWidth();
        double max = zone.getBounds().getX();
        for (BxLine line : zone.getLines()) {
            if (line.getBounds().getX() < min) {
                min = line.getBounds().getX();
            }
            if (line.getBounds().getX() > max) {
                max = line.getBounds().getX();
            }
        }
        return max - min;
    }

}
