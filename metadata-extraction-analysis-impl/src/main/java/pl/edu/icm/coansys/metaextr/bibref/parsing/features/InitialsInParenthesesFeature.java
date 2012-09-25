package pl.edu.icm.coansys.metaextr.bibref.parsing.features;

import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationToken;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class InitialsInParenthesesFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "InitialsInParentheses";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        String text = object.getText();
        int index = context.getTokens().indexOf(object);

        if (text.matches("^[A-Z]$")) {
            if (index - 1 < 0 || index + 2 >= context.getTokens().size()) {
                return 0;
            }
            if (context.getTokens().get(index - 1).getText().equals("(")
                    && context.getTokens().get(index + 1).getText().equals(".")
                    && context.getTokens().get(index + 2).getText().equals(")")) {
                return 1;
            }
        }
        if (text.equals(".")) {
            if (index - 2 < 0 || index + 1 >= context.getTokens().size()) {
                return 0;
            }
            if (context.getTokens().get(index - 2).getText().equals("(")
                    && context.getTokens().get(index - 1).getText().matches("^[A-Z]$")
                    && context.getTokens().get(index + 1).getText().equals(")")) {
                return 1;
            }
        }
        if (text.matches("^[A-Z][a-z]+$")) {
            if (index - 1 < 0 || index + 1 >= context.getTokens().size()) {
                return 0;
            }
            if (context.getTokens().get(index - 1).getText().equals("(")
                    && context.getTokens().get(index + 1).getText().equals(")")) {
                return 1;
            }
        }
        return 0;
    }

}
