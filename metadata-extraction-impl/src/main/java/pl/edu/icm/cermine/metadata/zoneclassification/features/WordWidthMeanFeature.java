package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxWord;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class WordWidthMeanFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "WordWidthMean";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int count = 0;
        double mean = 0;

        for (BxLine line : zone.getLines()) {
            for (BxWord word : line.getWords()) {
                count++;
                mean += word.getBounds().getWidth();
            }
        }
        return mean / (int) count;
    }

}
