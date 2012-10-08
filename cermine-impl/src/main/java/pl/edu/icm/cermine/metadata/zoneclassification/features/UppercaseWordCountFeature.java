package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class UppercaseWordCountFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int count = 0;
        for (BxLine line : zone.getLines()) {
            for (BxWord word : line.getWords()) {
                String s = "";
                for (BxChunk chunk : word.getChunks()) {
                    s += chunk.getText();
                }
                if (!s.isEmpty() && Character.isUpperCase(s.charAt(0))) {
                    count++;
                }
            }
        }
        return (double) count;
    }

}
