package pl.edu.icm.yadda.analysis.bibref.parsing.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class LowercaseRelativeCountFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "LowercaseRelativeCount";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        char[] charArray = object.getText().toCharArray();
        int lowercase = 0;
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isLowerCase(charArray[i])) {
                lowercase++;
            }
        }

        return (double) lowercase / (double) object.getText().length();
    }

}
