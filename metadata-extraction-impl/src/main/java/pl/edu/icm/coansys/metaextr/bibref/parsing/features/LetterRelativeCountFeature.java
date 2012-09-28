package pl.edu.icm.coansys.metaextr.bibref.parsing.features;

import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationToken;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class LetterRelativeCountFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "LetterRelativeCount";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        char[] charArray = object.getText().toCharArray();
        int letters = 0;
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isLetter(charArray[i])) {
                letters++;
            }
        }

        return (double) letters / (double) object.getText().length();
    }

}
