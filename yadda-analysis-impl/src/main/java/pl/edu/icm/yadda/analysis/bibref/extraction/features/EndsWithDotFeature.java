package pl.edu.icm.yadda.analysis.bibref.extraction.features;

import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class EndsWithDotFeature implements FeatureCalculator<BxLine, BxDocumentBibReferences> {

    private static String featureName = "EndsWithDot";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        String text = refLine.toText();
        if (Pattern.matches("^.*\\d\\.$", text)) {
            return 1;
        }
        return Pattern.matches("^.*\\.$", text) ? 0.5 : 0;
    }
    
}
