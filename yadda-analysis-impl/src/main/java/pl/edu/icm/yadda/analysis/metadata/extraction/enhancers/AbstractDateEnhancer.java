package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YDate;
import pl.edu.icm.yadda.bwmeta.model.YElement;

/**
 *
 * @author krusek
 */
abstract public class AbstractDateEnhancer extends AbstractPatternEnhancer {

    private static String[] MONTHS = {
        "january|jan\\.",
        "february|feb\\.",
        "march|mar\\.",
        "april|apr\\.",
        "may",
        "june|jun\\.",
        "july|jul\\.",
        "august|aug\\.",
        "september|sep\\.",
        "october|oct\\.",
        "november|nov\\.",
        "december|dec\\."};

    private EnhancedField field;
    private String dateType;

    public AbstractDateEnhancer(EnhancedField field, String type, String nameRegex) {
        super(createPattern(nameRegex));
        setSearchedZoneLabels(BxZoneLabel.DATES);
        this.field = field;
        this.dateType = type;
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(field);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, YElement metadata) {
        YDate date = new YDate().setType(dateType);
        date.setDay(Integer.parseInt(result.group(2)));
        for (int i = 0; i < 12; i++) {
            if (result.group(i + 3) != null) {
                date.setMonth(i + 1);
                break;
            }
        }
        date.setYear(Integer.parseInt(result.group(15)));
        date.setText(result.group(1));
        metadata.addDate(date);
        return true;
    }

    private static Pattern createPattern(String nameRegex) {
        String regex = "\\b"+nameRegex+"[\\s:-]\\s*((\\d{1,2})\\s+(?:";
        boolean first = true;
        for (String monthRegex : MONTHS) {
            if (first) {
                first = false;
            } else {
                regex += "|";
            }
            regex += "(" + monthRegex + ")";
        }
        regex += ")\\s+(\\d{4}))";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
}
