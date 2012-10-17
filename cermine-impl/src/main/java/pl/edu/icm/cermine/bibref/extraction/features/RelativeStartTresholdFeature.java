package pl.edu.icm.cermine.bibref.extraction.features;

import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class RelativeStartTresholdFeature extends FeatureCalculator<BxLine, BxDocumentBibReferences> {

    private static final int LINE_INDENT_TRESHOLD = 10;
    
    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        return (refLine.getBounds().getX() - refs.getZone(refLine).getBounds().getX() > LINE_INDENT_TRESHOLD) ? 1 : 0;
    }
    
}
