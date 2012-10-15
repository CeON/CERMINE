package pl.edu.icm.cermine.bibref.parsing.features;

import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsPagesTextFeature extends FeatureCalculator<CitationToken, Citation> {

    private static final String[] PAGES_STRINGS = {"p", "pp", "pages"};
    
    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        String text = object.getText();
        int index = context.getTokens().indexOf(object);
        List<CitationToken> tokens = context.getTokens();

        if (ArrayUtils.contains(PAGES_STRINGS, text)) {
            return 1;
        }
        if (index - 1 >= 0 && text.equals(".") && ArrayUtils.contains(PAGES_STRINGS, tokens.get(index - 1).getText())) {
            return 1;
        }
        return 0;
    }

}
