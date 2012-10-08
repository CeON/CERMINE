package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PunctuationRelativeCountFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "PunctuationRelativeCount";

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
