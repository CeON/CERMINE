package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class DescriptionEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern PREFIX = Pattern.compile("^Abstract[-:\\.]?", Pattern.CASE_INSENSITIVE);

    public DescriptionEnhancer() {
        setSearchedZoneLabels(EnumSet.of(BxZoneLabel.MET_ABSTRACT));
        setSearchedFirstPageOnly(true);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.DESCRIPTION);
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, Element metadata) {
        String text = "";
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                String[] lines = zone.toText().split("\n");
                for (String line : lines) {
                    if (line.toLowerCase().startsWith("keywords")
                        || line.toLowerCase().startsWith("key words")) {
                        break;
                    }
                    text += "\n" + line;
                }
            }
         }

        text = text.trim();
        if (!text.isEmpty()) {
            Matcher matcher = PREFIX.matcher(text);
            if (matcher.find()) {
                text = text.substring(matcher.end()).trim();
            }
            Enhancers.setAbstract(metadata, text);
            return true;
        }
        return false;
    }

}
