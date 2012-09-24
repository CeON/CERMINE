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
public class VolumeEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile("\\b(?:volume|vol\\.?)[\\s:-]\\s*(\\d+)",
            Pattern.CASE_INSENSITIVE);
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);

    public VolumeEnhancer() {
        super(PATTERN, SEARCHED_ZONE_LABELS);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.VOLUME);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, YElement metadata) {
        Enhancers.addVolume(metadata, result.group(1));
        return true;
    }
}
