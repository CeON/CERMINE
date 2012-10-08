package pl.edu.icm.cermine.bibref.parsing.features;

import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsSingleQuoteBetweenWordsFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsSingleQuoteBetweenWords";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        if (!object.getText().equals("\'") || object.getStartIndex() <= 0
                || object.getEndIndex() >= context.getText().length()) {
            return 0;
        }
        return (Character.isLetter(context.getText().charAt(object.getStartIndex() - 1))
                && Character.isLetter(context.getText().charAt(object.getEndIndex()))) ? 1 : 0;
    }
}
