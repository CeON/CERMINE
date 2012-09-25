package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YElement;

/**
 *
 * @author krusek
 */
public class PagesEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile(
            "\\bpp[\\s:-]\\s*(\\d+)[-\u002D\u00AD\u2010\u2011\u2012\u2013\u2014\u2015\u207B\u208B\u2212](\\d+)",
            Pattern.CASE_INSENSITIVE);

    public PagesEnhancer() {
        super(PATTERN);
        setSearchedZoneLabels(BxZoneLabel.MET_BIB_INFO);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, YElement metadata) {
        int first = Integer.parseInt(result.group(1));
        int last = Integer.parseInt(result.group(2));
        if (first <= last) {
            metadata.addAttribute("pages", first + "-" + last);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.PAGES);
    }

}
