package pl.edu.icm.yadda.analysis.zone.classification.features;

import pl.edu.icm.yadda.analysis.hmm.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

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
