package pl.edu.icm.cermine.bibref.parsing.features;

import java.util.List;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsNumberTextFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsNumberText";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        String text = object.getText();
        List<CitationToken> tokens = context.getTokens();
        int index = context.getTokens().indexOf(object);

        if (text.equalsIgnoreCase("no")) {
            return 1;
        }
        if (index + 1 < tokens.size() && text.equalsIgnoreCase("n")
                && tokens.get(index + 1).getText().equals("\u00B0")) {
            return 1;
        }
        if (index - 1 >= 0 && text.equalsIgnoreCase("\u00B0") && tokens.get(index - 1).getText().equals("n")) {
            return 1;
        }
        if (index + 1 < tokens.size() && text.equalsIgnoreCase("n")
                && tokens.get(index + 1).getText().equals(".")) {
            return 1;
        }
        if (index - 1 >= 0 && text.equalsIgnoreCase(".") && tokens.get(index - 1).getText().equals("n")) {
            return 1;
        }
        return 0;
    }

}
