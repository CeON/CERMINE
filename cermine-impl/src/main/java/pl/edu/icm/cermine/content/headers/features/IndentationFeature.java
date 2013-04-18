package pl.edu.icm.cermine.content.headers.features;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IndentationFeature extends FeatureCalculator<BxLine, BxPage> {

    private static final int MAX_LINES = 5;
    
    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        int i = 0;
        BxLine l = line;
        double meanX = 0;
        while (l.hasNext() && i < MAX_LINES) {
            l = l.getNext();
            if ((line.getX() < l.getX() && l.getX() < line.getX() + line.getWidth()) 
                    || (l.getX() < line.getX() && line.getX() < l.getX() + l.getWidth())) {
                meanX += l.getX();
                i++;
            } else {
                break;
            }
        }
        if (i == 0 || line.getWidth() == 0) {
            return 0.0;
        }
        
        meanX /= i;
        return Math.abs(line.getX() - meanX) / (double) line.getWidth();
    }
    
}
