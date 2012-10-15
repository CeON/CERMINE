package pl.edu.icm.cermine.bibref.parsing.features;

import org.apache.commons.lang.ArrayUtils;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsDashBetweenWordsFeature extends FeatureCalculator<CitationToken, Citation> {

    private static final char[] DASH_CHARS = {
        '-', '\u002D', '\u2010', '\u2011', '\u2012', '\u2013', '\u2014', '\u2015', '\u207B', '\u208B', '\u2212'};
    
    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        String text = object.getText();
        if (text.length() != 1 || object.getStartIndex() <= 0 || object.getEndIndex() >= context.getText().length()) {
            return 0;
        }
        if (!ArrayUtils.contains(DASH_CHARS, text.charAt(0))) {
            return 0;
        }
        return (Character.isLetter(context.getText().charAt(object.getStartIndex() - 1))
                && Character.isLetter(context.getText().charAt(object.getEndIndex()))) ? 1 : 0;
    }

}
