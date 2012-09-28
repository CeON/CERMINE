package pl.edu.icm.coansys.metaextr.bibref.parsing.features;

import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationToken;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsAllLettersOrDigitsFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsAllLettersOrDigits";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        char[] charArray = object.getText().toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (!Character.isLetterOrDigit(charArray[i])) {
                return 0;
            }
        }
        return 1;
    }

}
