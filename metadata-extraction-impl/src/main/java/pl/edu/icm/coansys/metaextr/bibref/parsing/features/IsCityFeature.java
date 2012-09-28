package pl.edu.icm.coansys.metaextr.bibref.parsing.features;

import java.util.Arrays;
import java.util.List;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationToken;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsCityFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsCity";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    private static List<String> keywords = Arrays.asList(
            "angeles", "antonio", "amsterdam", "ankara", "athens",
            "bangkok", "basel", "beijing", "belgrade", "berkeley", "berlin", "bern", "bologna", "bombay", "boston",
                "bratislava", "brussels", "bucharest", "budapest",
            "cambridge", "calgary", "chicago", "copenhagen",
            "dallas", "delhi", "dhaka", "diego", "dordrecht", "dublin",
            "edmonton",
            "francisco",
            "grenoble", "göttingen",
            "heidelberg", "helsinki", "houston",
            "indianapolis", "istanbul",
            "jakarta", "jacksonville", "jose",
            "karachi", "kiev",
            "leipzig", "lisbon", "ljubljana", "london", "londres", "los",
            "madrid", "manila", "mass", "minsk", "montreal", "moscou", "moscow", "mumbai",
            "new",
            "orsay", "oslo", "ottawa", "oxford",
            "paris", "phoenix", "philadelphia", "prague", "princeton", "providence",
            "reading", "reykjavik", "riga", "roma", "rome",
            "san", "sarajevo", "seoul", "shanghai", "skopje", "sofia", "stockholm",
            "tallinn", "tehran", "tirana", "tokyo", "toronto", "toulouse",
            "vancouver", "vienna", "vilnius",
            "warsaw", "warszawa",
            "york",
            "zagreb");
    

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        return (keywords.contains(object.getText().toLowerCase())) ? 1 : 0;
    }
}
