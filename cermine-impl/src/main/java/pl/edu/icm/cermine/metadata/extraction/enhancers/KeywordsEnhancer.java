package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

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
                String separator = null;
                if(text.contains(";")) {
                	separator = ";";
                } else if(text.contains(",")) {
                	separator = ",";
                } else if(text.contains(".")) {
                	separator = ".";
                } else {
                	separator = "\\s";
                }
                for (String keyword : text.split(separator)) {
                	if(keyword.length() > 0) {
                		Enhancers.addKeyword(metadata, keyword.trim().replaceFirst("\\.$", ""));
                	}
                }
                return true;
            }
        }
        return false;
    }
}
