package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class YearEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile("(\\d\\d\\d\\d)");
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);

    public YearEnhancer() {
        super(PATTERN, SEARCHED_ZONE_LABELS);
    }
    
    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.PUBLISHED_DATE);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, Element metadata) {
        for (int i = 1; i <= result.groupCount(); i++) {
            String year = result.group(i);
            try {
                int y = Integer.parseInt(year);
                if (y >= 1900 && y < 2020) {
                    Enhancers.setYear(metadata, year);
                    return true;
                }
            } catch (NumberFormatException e) {}
        }
        return false;
    }
}
