package pl.edu.icm.coansys.metaextr.content.features.line;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class LengthFeature implements FeatureCalculator<BxLine, BxPage> {

    private static String featureName = "Length";

    @Override
    public String getFeatureName() {
        return featureName;
    }

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
