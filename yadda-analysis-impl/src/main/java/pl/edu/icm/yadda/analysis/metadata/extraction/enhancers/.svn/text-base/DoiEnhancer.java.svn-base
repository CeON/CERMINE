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
public class DoiEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile("\\bdoi:\\s*(10\\.\\d{4}/\\S+)");
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);

    public DoiEnhancer() {
        super(PATTERN, SEARCHED_ZONE_LABELS);
    }
    
    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.DOI);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, YElement metadata) {
        metadata.addId(new YId(YConstants.EXT_SCHEME_DOI, result.group(1)));
        return true;
    }
}
