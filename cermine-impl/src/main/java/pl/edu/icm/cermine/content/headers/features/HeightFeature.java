package pl.edu.icm.cermine.content.headers.features;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class HeightFeature extends FeatureCalculator<BxLine, BxPage> {

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
