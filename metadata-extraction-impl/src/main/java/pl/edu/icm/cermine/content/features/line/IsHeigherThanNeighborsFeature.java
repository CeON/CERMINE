package pl.edu.icm.cermine.content.features.line;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsHeigherThanNeighborsFeature implements FeatureCalculator<BxLine, BxPage> {

    private static String featureName = "IsHeigherThanNeighbors";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        double score = 0;        
        
        double max = line.getHeight();
        double min = line.getHeight();
        
        BxLine l = line;
        int i = 0;
        while (l.hasPrev()) {
            if (i >= 2)
                break;
            l = l.getPrev();
            if (l.getHeight() > max)
                max = l.getHeight();
            if (l.getHeight() < min)
                min = l.getHeight();
            i++;
        }
        
        if (Math.abs(max - line.getHeight()) < 0.1 && Math.abs(min - line.getHeight()) > 1) {
            score += 0.5;
        }
        
        max = line.getHeight();
        min = line.getHeight();
        
        i = 0;
        l = line;
        while (l.hasNext()) {
            if (i >= 2)
                break;
            l = l.getNext();
            if (l.getHeight() > max)
                max = l.getHeight();
            if (l.getHeight() < min)
                min = l.getHeight();
            i++;
        }
        
        if (Math.abs(max - line.getHeight()) < 0.1 && Math.abs(min - line.getHeight()) > 1) {
            score += 0.5;
        }
        
        return score;
    }
    
}
