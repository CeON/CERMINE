package pl.edu.icm.cermine.bibref.parsing.features;

import java.util.Arrays;
import java.util.List;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsCommonSeriesWordFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsCommonSeriesWord";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    private static List<String> keywords = Arrays.asList(
            "ann", "appl", "applied", "astérisque",
            "graduate",
            "henri",
            "inst",
            "lect", "lecture", "lectures",
            "math", "mathematical", "mathematics", "maths",
            "note", "notes",
            "physics", "poincaré", "pure",
            "research",
            "series", "soc", "springer", "statistics",
            "texts"
            );
            

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        return (keywords.contains(object.getText().toLowerCase())) ? 1 : 0;
    }
}
