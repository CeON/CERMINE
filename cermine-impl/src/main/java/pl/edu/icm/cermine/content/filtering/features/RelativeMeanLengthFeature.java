package pl.edu.icm.cermine.content.filtering.features;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class RelativeMeanLengthFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        BxLine firstLine = zone.getLines().get(0);
        BxLine line = firstLine;
        double meanTotalWidth = line.getWidth();
        int lineCount = 1;
        
        while (line.hasPrev()) {
            line = line.getPrev();
            meanTotalWidth += line.getWidth();
            lineCount++;
        }
        line = firstLine;
        while (line.hasNext()) {
            line = line.getNext();
            meanTotalWidth += line.getWidth();
            lineCount++;
        }
        
        if (lineCount == 0) {
            return 0;
        }
        
        meanTotalWidth /= lineCount;
        
        double meanZoneWidth = 0;
        for (BxLine l : zone.getLines()) {
            meanZoneWidth += l.getWidth();
        }
        
        if (zone.getLines().isEmpty() || meanTotalWidth == 0) {
            return 0;
        }
        
        return meanZoneWidth / zone.getLines().size() / meanTotalWidth;
    }
    
}
