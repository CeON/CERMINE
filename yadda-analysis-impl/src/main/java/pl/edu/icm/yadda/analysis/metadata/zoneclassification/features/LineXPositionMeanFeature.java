package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class LineXPositionMeanFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "LineXPositionMean";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        double mean = 0;
        for (BxLine line : zone.getLines()) {
            mean += line.getBounds().getX();
        }
        return mean / (double) zone.getLines().size() - zone.getBounds().getX();
    }

}
