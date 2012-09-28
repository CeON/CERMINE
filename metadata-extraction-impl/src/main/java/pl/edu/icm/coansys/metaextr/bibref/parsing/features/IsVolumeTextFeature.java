package pl.edu.icm.coansys.metaextr.bibref.parsing.features;

import java.util.List;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationToken;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsVolumeTextFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsVolumeText";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        String text = object.getText();
        if (text.equalsIgnoreCase("vol") || text.equalsIgnoreCase("volume") || text.equalsIgnoreCase("tom")
                || text.equalsIgnoreCase("tome")) {
            return 1;
        }
        List<CitationToken> tokens = context.getTokens();
        int index = tokens.indexOf(object);
        if (index + 2 < tokens.size() && text.equalsIgnoreCase("t")) {
            if (tokens.get(index + 1).getText().matches("^\\d+$")) {
                return 1;
            }
            if (tokens.get(index + 1).getText().equals(".") && tokens.get(index + 2).getText().matches("^\\d+$")) {
                return 1;
            }
        }
        return 0;
    }

}
