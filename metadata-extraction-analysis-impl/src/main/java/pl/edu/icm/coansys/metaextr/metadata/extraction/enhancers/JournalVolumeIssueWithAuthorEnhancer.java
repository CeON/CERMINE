package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class JournalVolumeIssueWithAuthorEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile("(.*)\\d{4}, (\\d+):(\\d+)");
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);

    public JournalVolumeIssueWithAuthorEnhancer() {
        super(PATTERN, SEARCHED_ZONE_LABELS);
    }
    
    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.JOURNAL, EnhancedField.VOLUME, EnhancedField.ISSUE);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, Element metadata) {
        String journal = result.group(1);

        List<String> authors = Enhancers.getAuthorNames(metadata);

        if (authors.size() == 1) {
            journal = removeFirst(journal, authors.get(0));
        }
        if (authors.size() == 2) {
            journal = removeFirst(journal, authors.get(0));
            journal = removeFirst(journal, "and");
            journal = removeFirst(journal, authors.get(1));
        }
        if (authors.size() > 2) {
            journal = journal.replaceFirst("^.*et al\\.", "").trim();
        }

        Enhancers.setJournal(metadata, journal);
        Enhancers.setVolume(metadata, result.group(2));
        Enhancers.setIssue(metadata, result.group(3));
        
        return true;
    }

    private String removeFirst(String journal, String prefix) {
        if (journal.toLowerCase().startsWith(prefix.toLowerCase())) {
            return journal.substring(prefix.length()).trim();
        }
        String[] strs = prefix.split(" ");
        for (String str : strs) {
            if (journal.toLowerCase().startsWith(str.toLowerCase())) {
                return journal.substring(str.length()).trim();
            }
        }
        return journal;
    }
    
}
