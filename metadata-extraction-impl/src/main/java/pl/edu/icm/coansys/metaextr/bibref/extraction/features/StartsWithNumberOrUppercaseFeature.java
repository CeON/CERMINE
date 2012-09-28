package pl.edu.icm.coansys.metaextr.bibref.extraction.features;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.coansys.metaextr.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class StartsWithNumberOrUppercaseFeature implements FeatureCalculator<BxLine, BxDocumentBibReferences> {

    private static String featureName = "StartsWithNumberOrUppercase";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        String text = refLine.toText();
        List<BxLine> lines = refs.getLines();

        String[] patterns = {"^(\\d+).*", "^\\[(\\d+)\\].*"};
        for (String pText : patterns) {
            Pattern pattern = Pattern.compile(pText);
            Matcher matcher = pattern.matcher(text);
            if (!matcher.matches()) {
                continue;
            }

            int index = lines.indexOf(refLine);
            String objectMatch = matcher.group(1);
            String prevMatch = null;
            String nextMatch = null;
            for (int i = index - 1; i >= 0; i--) {
                BxLine line = lines.get(i);
                Matcher prevMatcher = pattern.matcher(line.toText());
                if (prevMatcher.matches()) {
                    prevMatch = prevMatcher.group(1);
                    break;
                }
            }
            for (int i = index + 1; i < lines.size(); i++) {
                BxLine line = lines.get(i);
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

        Pattern pattern = Pattern.compile("^([A-Z]+)\\W.*$");
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) {
            return 0;
        }

        int total = 0;
        for (BxLine line : refs.getLines()) {
            Matcher lineMatcher = pattern.matcher(line.toText());
            if (total == 0 && !lineMatcher.matches()) {
                return 0;
            }
            if (lineMatcher.matches()) {
                total++;
            }
        }

        return (total * 4 >= refs.getLines().size()) ? 1 : 0;
    }
    
}
