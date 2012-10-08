package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class ArticleIdEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile("\\barticle id[:-]? (\\d+)", Pattern.CASE_INSENSITIVE);
    
    public ArticleIdEnhancer() {
        super(PATTERN, EnumSet.of(BxZoneLabel.MET_BIB_INFO));
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.ARTICLE_ID);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, Element metadata) {
        Enhancers.addArticleId(metadata, "hindawi-id", result.group(1));
        return true;
    }
}
