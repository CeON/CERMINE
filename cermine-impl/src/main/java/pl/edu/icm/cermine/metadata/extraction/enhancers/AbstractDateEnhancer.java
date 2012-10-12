package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

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

    public AbstractDateEnhancer(EnhancedField field, String nameRegex) {
        super(createPattern(nameRegex));
        setSearchedZoneLabels(BxZoneLabel.MET_DATES);
        this.field = field;
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(field);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, Element metadata) {
        //YDate date = new YDate().setType(dateType);
        
        //date.setDay(Integer.parseInt(result.group(2)));
        String day = result.group(2);
        String month = null;
        for (int i = 0; i < 12; i++) {
            if (result.group(i + 3) != null) {
                month = String.valueOf(i + 1);
                break;
            }
        }
        String year = result.group(15);
        enhanceMetadata(metadata, day, month, year);
        return true;
    }
    
    protected abstract void enhanceMetadata(Element metadata, String day, String month, String year);

    private static Pattern createPattern(String nameRegex) {
        StringBuilder regex = new StringBuilder();
        regex.append("\\b");
        regex.append(nameRegex);
        regex.append("[\\s:-]\\s*((\\d{1,2})\\s+(?:");
        boolean first = true;
        for (String monthRegex : MONTHS) {
            if (first) {
                first = false;
            } else {
                regex.append("|");
            }
            regex.append("(");
            regex.append(monthRegex);
            regex.append(")");
        }
        regex.append(")\\s+(\\d{4}))");
        return Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE);
    }
}
