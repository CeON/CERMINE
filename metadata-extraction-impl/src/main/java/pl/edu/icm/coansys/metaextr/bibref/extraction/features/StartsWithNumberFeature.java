package pl.edu.icm.coansys.metaextr.bibref.extraction.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.coansys.metaextr.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class StartsWithNumberFeature implements FeatureCalculator<BxLine, BxDocumentBibReferences> {

    private static String featureName = "StartsWithNumber";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        String[] patterns = {"^\\[(\\d+)\\] .*", "^(\\d+)\\.? .*"};
        for (String pText : patterns) {
            Pattern pattern = Pattern.compile(pText);
            Matcher matcher = pattern.matcher(refLine.toText());
            if (!matcher.matches()) {
                continue;
            }
                       
            int index = refs.getLines().indexOf(refLine);
            String objectMatch = matcher.group(1);
            String prevMatch = null;
            String nextMatch = null;
            for (int i = index - 1; i >= 0; i--) {
                BxLine line = refs.getLines().get(i);
                Matcher prevMatcher = pattern.matcher(line.toText());
                if (prevMatcher.matches()) {
                    prevMatch = prevMatcher.group(1);
                    break;
                }
            }
            for (int i = index + 1; i < refs.getLines().size(); i++) {
                BxLine line = refs.getLines().get(i);
                Matcher nextMatcher = pattern.matcher(line.toText());
                if (nextMatcher.matches()) {
                    nextMatch = nextMatcher.group(1);
                    break;
                }
            }

            if (prevMatch != null && objectMatch != null
                    && Integer.parseInt(prevMatch) + 1 == Integer.parseInt(objectMatch)) {
                return 1;
            }

            if (nextMatch != null && objectMatch != null
                    && Integer.parseInt(objectMatch) + 1 == Integer.parseInt(nextMatch)) {
                return 1;
            }
        }

        return 0;
    }
    
}
