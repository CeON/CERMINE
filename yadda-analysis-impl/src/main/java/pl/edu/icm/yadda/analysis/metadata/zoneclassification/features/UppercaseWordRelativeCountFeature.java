package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxChunk;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class UppercaseWordRelativeCountFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "UppercaseWordRelativeCount";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int count = 0;
        int allCount = 0;
        for (BxLine line : zone.getLines()) {
            for (BxWord word : line.getWords()) {
                allCount++;
                String s = "";
                for (BxChunk chunk : word.getChunks()) {
                    s += chunk.getText();
                }
                if (!s.isEmpty() && Character.isUpperCase(s.charAt(0))) {
                    count++;
                }
            }
        }
        return (double) count / (double) allCount;
    }
}
