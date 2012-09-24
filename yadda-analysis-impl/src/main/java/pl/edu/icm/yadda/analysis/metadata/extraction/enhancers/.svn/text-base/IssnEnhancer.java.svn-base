package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YId;

/**
 *
 * @author krusek
 */
public class IssnEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile(
            "\\bISSN:?\\s*(\\d{4}[-\u002D\u00AD\u2010\u2011\u2012\u2013\u2014\u2015\u207B\u208B\u2212]\\d{3}[\\dX])\\b",
            Pattern.CASE_INSENSITIVE);
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);

    public IssnEnhancer() {
        super(PATTERN, SEARCHED_ZONE_LABELS);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.ISSN);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, YElement metadata) {
        metadata.addId(new YId(YConstants.EXT_SCHEME_ISSN, result.group(1)));
        return true;
    }
}
