package pl.edu.icm.yadda.analysis.articlecontent.features.line;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class PrevSpaceFeature implements FeatureCalculator<BxLine, BxPage> {

    private static String featureName = "PrevSpaceFeature";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        if (!line.hasPrev() || line.getPrev().getY() > line.getY()) {
            return 0;
        }
        
        double space = line.getY() - line.getPrev().getY();
        
        BxLine l = line;
        int i = 0;
        while (l.hasPrev()) {
            l = l.getPrev();
            if (!l.hasPrev()) {
                break;
            }
            if (i >= 4 || l.getPrev().getY() > l.getY())
                break;
            if (l.getY() - l.getPrev().getY() > space) {
                space = l.getY() - l.getPrev().getY();
            }
            i++;
        }
                
        return (Math.abs(space - line.getY() + line.getPrev().getY()) < 0.1) ? 1 : 0;
    }
    
}
