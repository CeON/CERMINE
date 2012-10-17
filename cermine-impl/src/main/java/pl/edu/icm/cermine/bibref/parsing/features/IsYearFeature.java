package pl.edu.icm.cermine.bibref.parsing.features;

import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsYearFeature extends FeatureCalculator<CitationToken, Citation> {
    
    private static final int MIN_YEAR = 1800;
    private static final int MAX_YEAR = 2100;

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        try {
            int year = Integer.parseInt(object.getText());
            return (year >= MIN_YEAR && year < MAX_YEAR) ? 1 : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
