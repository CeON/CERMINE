package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class EditorEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern PREFIX_PATTERN = Pattern.compile(
            "^\\s*(?:academic\\s*)?editor:",
            Pattern.CASE_INSENSITIVE);
    
    public EditorEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_EDITOR);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.EDITOR);
    }
    
    @Override
    protected boolean enhanceMetadata(BxZone zone, Element metadata) {
        String text = zone.toText();
        Matcher matcher = PREFIX_PATTERN.matcher(text);
        if (matcher.find()) {
            text = text.substring(matcher.end()).trim();
        }
        Enhancers.addEditor(metadata, text);
        return true;
    }
}
