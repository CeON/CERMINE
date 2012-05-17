package pl.edu.icm.yadda.analysis.bibref.parsing.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.yadda.analysis.hmm.features.FeatureCalculator;
/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsCommonSourceWordFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsCommonSourceWord";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    private static List<String> keywords = Arrays.asList(
            "acad", "acta", "algebra", "amer", "anal", "ann", "annales", "annals", "appl",
            "bourbaki", "bull",
            "comm", "comptes",
            "fields", "fourier",
            "geom",
            "inst", "invent",
            "journal",
            "lett",
            "mat", "math", "mathematical", "mathematics",
            "phys", "physics", "probab", "proc", "publ", "pure",
            "séminaire", "sc", "sci", "sciences", "soc", "studies",
            "theory", "trans",
            "univ"
            );

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        return (keywords.contains(object.getText().toLowerCase())) ? 1 : 0;
    }
}
