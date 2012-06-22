package pl.edu.icm.yadda.analysis.bibref.parsing.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsDashFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsDash";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        String text = object.getText();
        return (text.length() == 1 && (text.charAt(0) == '-' || text.charAt(0) == '\u002D' || text.charAt(0) == '\u00AD'
                || text.charAt(0) == '\u2010' || text.charAt(0) == '\u2011' || text.charAt(0) == '\u2012'
                || text.charAt(0) == '\u2013' || text.charAt(0) == '\u2014' || text.charAt(0) == '\u2015'
                || text.charAt(0) == '\u207B' || text.charAt(0) == '\u208B' || text.charAt(0) == '\u2212')) ? 1 : 0;
    }

}
