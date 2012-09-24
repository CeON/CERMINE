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
public class HindawiCornerInfoEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile(
            "(.+)\n(.+)\nVolume \\d+, Article ID \\d+, \\d+ pages\ndoi:.+");

    public HindawiCornerInfoEnhancer() {
        super(PATTERN);
        setSearchedZoneLabels(BxZoneLabel.MET_BIB_INFO);
        setSearchedFirstPageOnly(true);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, YElement metadata) {
        Enhancers.addPublisher(metadata, result.group(1));
        Enhancers.addJournal(metadata, result.group(2));
        return true;
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.JOURNAL, EnhancedField.PUBLISHER);
    }

}
