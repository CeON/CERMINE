package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

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
    protected boolean enhanceMetadata(BxDocument document, Element metadata) {
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                String text = zone.toText().replace("\n", " ");
                text = PREFIX.matcher(text).replaceFirst("");
                String separator = text.indexOf(";") > -1 ? ";" : ",";
                for (String keyword : text.split(separator)) {
                    Enhancers.addKeyword(metadata, keyword.trim().replaceFirst("\\.$", ""));
                }
                return true;
            }
        }
        return false;
    }
}
