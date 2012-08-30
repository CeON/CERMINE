package pl.edu.icm.yadda.analysis.bibref.extraction.features;

import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class PrevEndsWithDotFeature implements FeatureCalculator<BxLine, BxDocumentBibReferences> {

    private static String featureName = "PrevEndsWithDot";

    @Override
    public String getFeatureName() {
        return featureName;
    }

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
