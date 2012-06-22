package pl.edu.icm.yadda.analysis.bibref.parsing.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsYearFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsYear";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        try {
            int year = Integer.parseInt(object.getText());
            return (year > 1699 && year < 2100) ? 1 : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
