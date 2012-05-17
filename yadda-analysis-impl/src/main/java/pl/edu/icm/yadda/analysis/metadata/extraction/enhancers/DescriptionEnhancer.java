package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YDescription;
import pl.edu.icm.yadda.bwmeta.model.YElement;

/**
 *
 * @author krusek
 */
public class DescriptionEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern PREFIX = Pattern.compile("^Abstract[-:\\.]?", Pattern.CASE_INSENSITIVE);

    public DescriptionEnhancer() {
        setSearchedZoneLabels(EnumSet.of(BxZoneLabel.ABSTRACT));
        setSearchedFirstPageOnly(true);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.DESCRIPTION);
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, YElement metadata) {
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
            metadata.addDescription(new YDescription().setType(YConstants.DS_ABSTRACT).setText(text));
            return true;
        }
        return false;
    }

}
