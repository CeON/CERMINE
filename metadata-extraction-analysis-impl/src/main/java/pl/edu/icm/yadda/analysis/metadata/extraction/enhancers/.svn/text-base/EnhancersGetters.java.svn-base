package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.regex.Pattern;
import pl.edu.icm.yadda.bwmeta.model.YAffiliation;
import pl.edu.icm.yadda.bwmeta.model.YAncestor;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YCurrent;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YName;
import pl.edu.icm.yadda.bwmeta.model.YStructure;

/**
 *
 * @author krusek
 */
public class Enhancers {

    //private static final Pattern AFFILIATION_INDEX_PATTERN = Pattern.compile("\\d+");
    // affiliation index cannot have more than 2 digits -- acz
    private static final Pattern AFFILIATION_INDEX_PATTERN = Pattern.compile("\\d{1,2}");
    private static final String AFFILIATION_DEFAULT_ID = "aff.default";

    public static boolean isAffiliationIndex(String text) {
        return AFFILIATION_INDEX_PATTERN.matcher(text).matches();
    }

    public static String affiliationIdFromIndex(String index) {
        return "aff." + index;
    }

    public static String affiliationIdFromRef(String ref) {
        if (isAffiliationIndex(ref)) {
            return affiliationIdFromIndex(ref);
        } else if (ref.equals("")) {
            return AFFILIATION_DEFAULT_ID;
        } else {
            throw new IllegalArgumentException("Invalid affiliation ref format");
        }
    }

    public static YStructure getOrCreateJournalStructure(YElement element) {
        YStructure structure = element.getStructure(YConstants.EXT_HIERARCHY_JOURNAL);
        if (structure == null) {
            structure = new YStructure(YConstants.EXT_HIERARCHY_JOURNAL);
            structure.setCurrent(new YCurrent(YConstants.EXT_LEVEL_JOURNAL_ARTICLE));
            element.addStructure(structure);
        }
        return structure;
    }

    public static YAffiliation getOrCreateAffiliationByIndex(YElement element, String index) {
        return getOrCreateAffiliation(element, affiliationIdFromIndex(index));
    }

    public static YAffiliation getOrCreateAffiliationByRef(YElement element, String ref) {
        return getOrCreateAffiliation(element, affiliationIdFromRef(ref));
    }

    public static YAffiliation getOrCreateAffiliation(YElement element, String id) {
        YAffiliation affiliation = element.getAffiliation(id);
        if (affiliation == null) {
            affiliation = new YAffiliation().setId(id);
            element.addAffiliation(affiliation);
        }
        return affiliation;
    }

    public static void addPublisher(YElement element, String publisher) {
        YStructure structure = Enhancers.getOrCreateJournalStructure(element);
        YAncestor ancestor = new YAncestor(YConstants.EXT_LEVEL_JOURNAL_PUBLISHER);
        ancestor.addName(new YName(publisher));
        structure.addAncestor(ancestor);
    }

    public static void addJournal(YElement element, String journal) {
        YStructure structure = Enhancers.getOrCreateJournalStructure(element);
        YAncestor ancestor = new YAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL);
        ancestor.addName(new YName(journal));
        structure.addAncestor(ancestor);
    }

    public static void addVolume(YElement element, String volume) {
        YStructure structure = getOrCreateJournalStructure(element);
        YAncestor ancestor = new YAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME);
        ancestor.addName(new YName(volume));
        structure.addAncestor(ancestor);
    }

    public static void addIssue(YElement element, String issue) {
        YStructure structure = getOrCreateJournalStructure(element);
        YAncestor ancestor = new YAncestor(YConstants.EXT_LEVEL_JOURNAL_ISSUE);
        ancestor.addName(new YName(issue));
        structure.addAncestor(ancestor);
    }
}
