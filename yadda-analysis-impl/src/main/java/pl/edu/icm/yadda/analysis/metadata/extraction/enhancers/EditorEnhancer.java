package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YName;

/**
 *
 * @author krusek
 */
public class EditorEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern PREFIX_PATTERN = Pattern.compile(
            "^\\s*(?:academic\\s*)?editor:",
            Pattern.CASE_INSENSITIVE);
    
    public EditorEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.EDITOR);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.EDITOR);
    }
    
    private static void putEditor(YElement element, String editor) {
        YName name = new YName().setType(YConstants.NM_CANONICAL).setText(editor);
        YContributor contributor = new YContributor().setRole(YConstants.CR_EDITOR).addName(name);
        element.addContributor(contributor);
    }

    @Override
    protected boolean enhanceMetadata(BxZone zone, YElement metadata) {
        String text = zone.toText();
        Matcher matcher = PREFIX_PATTERN.matcher(text);
        if (matcher.find()) {
            text = text.substring(matcher.end()).trim();
        }
        putEditor(metadata, text);
        return true;
    }
}
