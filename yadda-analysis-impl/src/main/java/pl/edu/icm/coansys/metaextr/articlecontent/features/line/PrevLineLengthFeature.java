package pl.edu.icm.coansys.metaextr.articlecontent.features.line;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxLine;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class PrevLineLengthFeature implements FeatureCalculator<BxLine, BxPage> {

    private static String featureName = "PrevLineLength";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        if (!line.hasPrev()) {
            return 1.0;
        }
        double avLength = 0;
        int linesCount = 0;
        for (BxZone zone : page.getZones()) {
            for (BxLine l : zone.getLines()) {
                linesCount++;
                avLength += l.getBounds().getWidth();
            }
        }
        
        avLength /= linesCount;
        
        return line.getPrev().getBounds().getWidth() / avLength;
    }
    
}
