package pl.edu.icm.yadda.analysis.relations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for easy interaction with the person directory.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * @see pl.edu.icm.yadda.analysis.relations.PersonDirectory
 *
 */
public class PersonDirectoryUtils {

    private static final Pattern CONTRIBUTION_PATTERN = Pattern.compile("(.+)#([0-9]+)");
    
    /**
     * Constructs a contribution identifier based on a document identifier
     * and the contributor's position on the list of document's contributors.
     * 
     * @param documentId Document to which the given contribution applies.
     * @param position Contributor's position on the list of contributors.
     * @return Contribution identifier.
     */
    public static String contrId(String documentId, int position) {
        return documentId + "#" + Integer.toString(position);
    }
    
    /**
     * Extracts contributor's position from a contribution identifier.
     * @param contributionId Contribution identifier.
     * @return Contributor's position within the document to which they contributed.
     */
    public static int positionFromContrId(String contributionId) {
        Matcher matcher = CONTRIBUTION_PATTERN.matcher(contributionId);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid contribution identifier");
        return new Integer(matcher.group(2));
    }

    /**
     * Extracts document identifier from a contribution identifier.
     * @param contributionId Contribution identifier.
     * @return Document identifier to which the contribution applies.
     */
    public static String documentFromContrId(String contributionId) {
        Matcher matcher = CONTRIBUTION_PATTERN.matcher(contributionId);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid contribution identifier");
        return matcher.group(1);
    }
}
