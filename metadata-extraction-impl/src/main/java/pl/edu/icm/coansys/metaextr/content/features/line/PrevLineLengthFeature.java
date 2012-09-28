package pl.edu.icm.coansys.metaextr.content.features.line;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

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
