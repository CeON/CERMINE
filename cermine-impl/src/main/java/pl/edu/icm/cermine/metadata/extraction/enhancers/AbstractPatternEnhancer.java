package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.Collection;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public abstract class AbstractPatternEnhancer extends AbstractSimpleEnhancer {

    private Pattern pattern;

    protected AbstractPatternEnhancer(Pattern pattern, Collection<BxZoneLabel> zoneLabels) {
        super(zoneLabels);
        this.pattern = pattern;
    }

    protected AbstractPatternEnhancer(Pattern pattern) {
        this.pattern = pattern;
    }

    protected void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    protected abstract boolean enhanceMetadata(MatchResult result, Element metadata);

    @Override
    protected boolean enhanceMetadata(BxZone zone, Element metadata) {
        Matcher matcher = pattern.matcher(zone.toText());
        while (matcher.find()) {
            if(enhanceMetadata(matcher.toMatchResult(), metadata)) {
                return true;
            }
        }
        return false;
    }
}
