package pl.edu.icm.yadda.analysis.bibref.parsing.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class UppercaseRelativeCountFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "UppercaseRelativeCount";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        char[] charArray = object.getText().toCharArray();
        int uppercase = 0;
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isUpperCase(charArray[i])) {
                uppercase++;
            }
        }

        return (double) uppercase / (double) object.getText().length();
    }

}
