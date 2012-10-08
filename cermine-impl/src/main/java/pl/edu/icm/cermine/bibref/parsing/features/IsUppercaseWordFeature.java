package pl.edu.icm.cermine.bibref.parsing.features;

import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsUppercaseWordFeature extends FeatureCalculator<CitationToken, Citation> {

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        if (object.getText().length() < 2) {
            return 0;
        }
        if (!Character.isUpperCase(object.getText().charAt(0))) {
            return 0;
        }
        for (int i = 1; i < object.getText().length(); i++) {
            if (!Character.isLowerCase(object.getText().charAt(i))) {
                return 0;
            }
        }
        return 1;
    }

}
