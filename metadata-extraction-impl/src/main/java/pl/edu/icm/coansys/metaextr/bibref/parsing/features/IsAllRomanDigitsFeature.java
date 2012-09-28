package pl.edu.icm.coansys.metaextr.bibref.parsing.features;

import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationToken;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsAllRomanDigitsFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsAllRomanDigits";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        char[] charArray = object.getText().toCharArray();
        for (int i = 0; i < charArray.length; i++) {
             if (charArray[i] != 'I' && charArray[i] != 'V' && charArray[i] != 'X' && charArray[i] != 'L'
                     && charArray[i] != 'C' && charArray[i] != 'D' && charArray[i] != 'M') {
                 return 0;
            }
        }
        return 1;
    }

}
