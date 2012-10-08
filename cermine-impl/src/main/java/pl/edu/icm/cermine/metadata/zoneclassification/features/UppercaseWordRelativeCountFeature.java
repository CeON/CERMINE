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
public class UppercaseWordRelativeCountFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int count = 0;
        int allCount = 0;
        for (BxLine line : zone.getLines()) {
            for (BxWord word : line.getWords()) {
                allCount++;
                if(!word.getChunks().isEmpty()) {
                	String s = word.getChunks().get(0).toText();
                	if (!s.isEmpty() && Character.isUpperCase(s.charAt(0))) {
                		count++;
                	}
                }
            }
        }
        return (double) count / (double) allCount;
    }
}
