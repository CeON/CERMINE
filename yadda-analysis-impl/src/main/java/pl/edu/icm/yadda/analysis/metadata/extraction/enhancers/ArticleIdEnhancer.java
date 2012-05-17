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
public class ArticleIdEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile("\\barticle id[:-]? (\\d+)", Pattern.CASE_INSENSITIVE);
    
    public ArticleIdEnhancer() {
        super(PATTERN, EnumSet.of(BxZoneLabel.BIB_INFO));
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.ARTICLE_ID);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, YElement metadata) {
        metadata.addAttribute("ArticleID", result.group(1));
        return true;
    }
}
