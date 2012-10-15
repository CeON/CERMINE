package pl.edu.icm.cermine.bibref.extraction.features;

import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class SpaceBetweenLinesFeature extends FeatureCalculator<BxLine, BxDocumentBibReferences> {

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        double minSpace = Double.POSITIVE_INFINITY;
        double maxSpace = Double.NEGATIVE_INFINITY;
        double lineSpace = 0;
        BxLine prevLine = null;
        for (BxLine line : refs.getLines()) {
            if (prevLine != null && line.getBounds().getY() > prevLine.getBounds().getY()) {
                double difference = line.getBounds().getY() - prevLine.getBounds().getY();
                if (minSpace > difference) {
                    minSpace = difference;
                }
                if (maxSpace < difference) {
                    maxSpace = difference;
                }
                if (line.equals(refLine)) {
                    lineSpace = difference;
                }
            }
            prevLine = line;
        }
        
        if (refs.getLines().indexOf(refLine) == 0 && maxSpace > minSpace * 1.2) {
            return 1;
        }
        
        return (lineSpace > minSpace * 1.2) ? 1 : 0;
    }
    
}
