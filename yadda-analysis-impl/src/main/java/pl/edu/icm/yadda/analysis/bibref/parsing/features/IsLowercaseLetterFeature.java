package pl.edu.icm.yadda.analysis.bibref.parsing.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsLowercaseLetterFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsLowercaseLetter";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        return (object.getText().matches("^[a-z]$")) ? 1 : 0;
    }
}
