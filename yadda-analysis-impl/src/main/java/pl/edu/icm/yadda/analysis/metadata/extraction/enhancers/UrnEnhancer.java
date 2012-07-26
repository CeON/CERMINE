package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YId;

/**
 *
 * @author krusek
 */
public class UrnEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile("\\bURN:?\\s*(\\S+)",
            Pattern.CASE_INSENSITIVE);

    public UrnEnhancer() {
        super(PATTERN, EnumSet.of(BxZoneLabel.MET_BIB_INFO));
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, YElement metadata) {
        // FIXME: Scheme for urn?
        metadata.addId(new YId("urn", result.group(1)));
        return true;
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.URN);
    }
}
