package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class PublisherEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile(
            "\\bpublisher[\\s:-]\\s*(.+)",
            Pattern.CASE_INSENSITIVE);
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);

    public PublisherEnhancer() {
        super(PATTERN, SEARCHED_ZONE_LABELS);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.PUBLISHER);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, Element metadata) {
        Enhancers.setPublisher(metadata, result.group(1));
        return true;
    }
}
