package pl.edu.icm.coansys.metaextr.bibref.parsing.features;

import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationToken;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class StartsWithUppercaseFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "StartsWithUppercase";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        return (object.getText().length() > 0 && Character.isUpperCase(object.getText().charAt(0))) ? 1 : 0;
    }

}
