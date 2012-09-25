package pl.edu.icm.coansys.metaextr.articlecontent.features.line;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxLine;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class HeightFeature implements FeatureCalculator<BxLine, BxPage> {

    private static String featureName = "Height";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        double meanHeight = 0;
        int lineCount = 0;
        for (BxZone z : page.getZones()) {
            for (BxLine l : z.getLines()) {
                meanHeight += l.getHeight();
                lineCount++;
            }
        }
        meanHeight /= lineCount;
        return line.getBounds().getHeight() / meanHeight;
    }
    
}
