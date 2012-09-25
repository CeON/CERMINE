package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YDate;
import pl.edu.icm.yadda.bwmeta.model.YElement;

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
    protected boolean enhanceMetadata(MatchResult result, YElement metadata) {
        for (int i = 1; i <= result.groupCount(); i++) {
            String year = result.group(i);
            try {
                int y = Integer.parseInt(year);
                if (y >= 1900 && y < 2020) {
                    metadata.addDate(new YDate().setType(YConstants.DT_PUBLISHED).setYear(y).setText(year));
                    return true;
                }
            } catch (NumberFormatException e) {}
        }
        return false;
    }
}
