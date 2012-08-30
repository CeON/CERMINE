package pl.edu.icm.yadda.analysis.bibref.extraction.features;

import pl.edu.icm.yadda.analysis.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class SpaceBetweenLinesFeature implements FeatureCalculator<BxLine, BxDocumentBibReferences> {

    private static String featureName = "SpaceBetweenLines";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        double minSpace = Double.POSITIVE_INFINITY;
        double maxSpace = Double.NEGATIVE_INFINITY;
        double lineSpace = 0;
        BxLine prevLine = null;
        for (BxLine line : refs.getLines()) {
            if (prevLine != null) {
                if (line.getBounds().getY() > prevLine.getBounds().getY()) {
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
            }
            prevLine = line;
        }
        
        if (refs.getLines().indexOf(refLine) == 0 && maxSpace > minSpace * 1.2) {
            return 1;
        }
        
        return (lineSpace > minSpace * 1.2) ? 1 : 0;
    }
    
}
