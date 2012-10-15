package pl.edu.icm.cermine.bibref.parsing.features;

import org.apache.commons.lang.ArrayUtils;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsDashFeature extends FeatureCalculator<CitationToken, Citation> {

    private static final char[] DASH_CHARS = {
        '-', '\u002D', '\u2010', '\u2011', '\u2012', '\u2013', '\u2014', '\u2015', '\u207B', '\u208B', '\u2212'};
    
    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        String text = object.getText();
        return (text.length() == 1 && ArrayUtils.contains(DASH_CHARS, text.charAt(0))) ? 1 : 0;
    }

}
