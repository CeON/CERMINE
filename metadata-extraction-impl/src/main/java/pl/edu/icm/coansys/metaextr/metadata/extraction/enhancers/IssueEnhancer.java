package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class IssueEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile("\\b(?:(?:issue|num|no\\.?)[\\s:-]\\s*|(?:volume|vol\\.?)[\\s:-]\\s*\\d+/)(\\d+)",
            Pattern.CASE_INSENSITIVE);
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);

    public IssueEnhancer() {
        super(PATTERN, SEARCHED_ZONE_LABELS);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.ISSUE);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, Element metadata) {
        Enhancers.setIssue(metadata, result.group(1));
        return true;
    }
}
