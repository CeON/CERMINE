package pl.edu.icm.cermine.bibref.extraction.features;

import java.util.regex.Pattern;
import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class PrevEndsWithDotFeature extends FeatureCalculator<BxLine, BxDocumentBibReferences> {

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        if (refs.getLines().indexOf(refLine) == 0) {
            return 0.9;
        }
        String text = refs.getLines().get(refs.getLines().indexOf(refLine)-1).toText();
        if (Pattern.matches("^.*\\d\\.$", text)) {
            return 1;
        }
        return Pattern.matches("^.*\\.$", text) ? 0.8 : 0;
    }
    
}
