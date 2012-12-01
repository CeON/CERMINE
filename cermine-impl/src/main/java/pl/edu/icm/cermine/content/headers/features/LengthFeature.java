package pl.edu.icm.cermine.content.headers.features;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class LengthFeature extends FeatureCalculator<BxLine, BxPage> {

    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        double avLength = line.getWidth();
        int linesCount = 1;
        BxLine l = line;
        while (l.hasPrev()) {
            l = l.getPrev();
            avLength += l.getWidth();
            linesCount++;
        }
        
        l = line;
        while (l.hasNext()) {
            l = l.getNext();
            avLength += l.getWidth();
            linesCount++;
        }
        
        avLength /= linesCount;
                
        return line.getBounds().getWidth() / avLength;
    }
    
}
