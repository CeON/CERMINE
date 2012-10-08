package pl.edu.icm.cermine.bibref.parsing.features;

import java.util.List;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsPagesTextFeature extends FeatureCalculator<CitationToken, Citation> {

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        String text = object.getText();
        int index = context.getTokens().indexOf(object);
        List<CitationToken> tokens = context.getTokens();

        if (text.equals("p") || text.equals("pp") || text.equals("pages")) {
            return 1;
        }
        if (index - 1 >= 0 && text.equals(".") && (tokens.get(index - 1).getText().equals("p") 
                || tokens.get(index - 1).getText().equals("pp") || tokens.get(index - 1).getText().equals("pages"))) {
            return 1;
        }
        return 0;
    }

}
