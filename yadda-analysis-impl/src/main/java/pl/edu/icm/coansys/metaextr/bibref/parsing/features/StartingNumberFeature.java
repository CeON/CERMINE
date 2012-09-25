package pl.edu.icm.coansys.metaextr.bibref.parsing.features;

import java.util.List;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationToken;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class StartingNumberFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "StartingNumber";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        List<CitationToken> tokens = context.getTokens();
        int index = context.getTokens().indexOf(object);

        if (tokens.size() > 0) {
            if (tokens.get(0).getText().matches("^\\d+$") && index == 0) {
                return 1;
            }
        }

        if (tokens.size() > 1) {
            String two = tokens.get(0).getText() + tokens.get(1).getText();
            if (two.matches("^\\d+\\.$") && index < 2) {
                return 1;
            }
        }

        if (tokens.size() > 2) {
            String three = tokens.get(0).getText() + tokens.get(1).getText() + tokens.get(2).getText();
            if (three.matches("^\\[\\d+\\]$") && index < 3) {
                return 1;
            }
            if (three.matches("^.\\d+\\.$") && index < 3) {
                return 1;
            }
        }

        return 0;
    }
}
