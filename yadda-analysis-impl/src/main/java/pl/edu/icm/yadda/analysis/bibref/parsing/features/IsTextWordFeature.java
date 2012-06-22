package pl.edu.icm.yadda.analysis.bibref.parsing.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsTextWordFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsOtherWord";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    private static List<String> keywords = Arrays.asList(
            "preprint", "preparation", "submitted", "phd", "thesis", "available", "thèse", "doctorale", "paraître",
            "appear", "proceeding", "proceedings");


    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        return (keywords.contains(object.getText().toLowerCase())) ? 1 : 0;
    }
}
