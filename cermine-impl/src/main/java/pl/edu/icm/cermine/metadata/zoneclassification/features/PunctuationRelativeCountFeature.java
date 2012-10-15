package pl.edu.icm.cermine.metadata.zoneclassification.features;

import org.apache.commons.lang.ArrayUtils;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PunctuationRelativeCountFeature extends FeatureCalculator<BxZone, BxPage> {

    private static final char[] PUNCT_CHARS = {'.', ',', '[', ']', ':', '-'};
    
    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int punctuationCount = 0;
        for (char character : zone.toText().toCharArray()) {
            if (ArrayUtils.contains(PUNCT_CHARS, character)) {
                punctuationCount++;
            }
        }
        return (double)punctuationCount / (double)zone.toText().length();
    }

}
