package pl.edu.icm.cermine.bibref.extraction.features;

import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class PrevRelativeLengthFeature extends FeatureCalculator<BxLine, BxDocumentBibReferences> {

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        if (refs.getLines().indexOf(refLine) == 0) {
            return 0.2;
        }
        
        BxLine prev = refs.getLines().get(refs.getLines().indexOf(refLine)-1);        
        return prev.getBounds().getWidth() / refs.getZone(prev).getBounds().getWidth();
    }
    
}
