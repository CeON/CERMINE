package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YTagList;

/**
 *
 * @author krusek
 */
public class KeywordsEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern PREFIX = Pattern.compile("^key\\s?words[:-]?", Pattern.CASE_INSENSITIVE);

    public KeywordsEnhancer() {
        setSearchedZoneLabels(EnumSet.of(BxZoneLabel.MET_KEYWORDS));
        setSearchedFirstPageOnly(true);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.KEYWORDS);
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, YElement metadata) {
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                YTagList tagList = new YTagList().setType(YConstants.TG_KEYWORD);
                String text = zone.toText().replace("\n", " ");
                text = PREFIX.matcher(text).replaceFirst("");
                String separator = text.indexOf(";") > -1 ? ";" : ",";
                for (String keyword : text.split(separator)) {
                    tagList.addValue(keyword.trim().replaceFirst("\\.$", ""));
                }
                metadata.addTagList(tagList);
                return true;
            }
        }
        return false;
    }
}
