package pl.edu.icm.yadda.analysis.zone.classification.features;

import pl.edu.icm.yadda.analysis.hmm.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PunctuationRelativeCountFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "ZReferences";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int punctuationCount = 0;
        for (char character : zone.toText().toCharArray()) {
            if (character == '.' || character == ','
                    || character == '[' || character == ']'
                    || character == ':' || character == '-') {
                punctuationCount++;
            }
        }
        return (double)punctuationCount / (double)zone.toText().length();
    }

}
