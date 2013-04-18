package pl.edu.icm.cermine.content.headers.features;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class PrevLineLengthFeature extends FeatureCalculator<BxLine, BxPage> {

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

        if (linesCount == 0 || avLength == 0) {
            return 0;
        }
        
        avLength /= linesCount;
        
        return line.getPrev().getBounds().getWidth() / avLength;
    }
    
}
