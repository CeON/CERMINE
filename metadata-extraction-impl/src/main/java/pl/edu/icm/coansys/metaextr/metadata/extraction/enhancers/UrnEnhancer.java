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
public class UrnEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile("\\bURN:?\\s*(\\S+)",
            Pattern.CASE_INSENSITIVE);

    public UrnEnhancer() {
        super(PATTERN, EnumSet.of(BxZoneLabel.MET_BIB_INFO));
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, Element metadata) {
        // FIXME: Scheme for urn?
        Enhancers.addArticleId(metadata, "urn", result.group(1));
        return true;
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.URN);
    }
}
