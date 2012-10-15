package pl.edu.icm.cermine.bibref.parsing.features;

import org.apache.commons.lang.ArrayUtils;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsAllRomanDigitsFeature extends FeatureCalculator<CitationToken, Citation> {

    private static final char[] ROMAN_CHARS = {'I', 'V', 'X', 'L', 'C', 'D', 'M'};
    
    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        char[] charArray = object.getText().toCharArray();
        for (int i = 0; i < charArray.length; i++) {
             if (!ArrayUtils.contains(ROMAN_CHARS, charArray[i])) {
                 return 0;
            }
        }
        return 1;
    }

}
