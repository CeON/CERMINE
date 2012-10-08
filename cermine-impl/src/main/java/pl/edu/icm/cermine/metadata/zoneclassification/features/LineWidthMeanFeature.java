package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class LineWidthMeanFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "LineWidthMean";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        double mean = 0;
        for (BxLine line : zone.getLines()) {
            mean += line.getBounds().getWidth();
        }
        return mean / (double) zone.getLines().size();
    }

}
