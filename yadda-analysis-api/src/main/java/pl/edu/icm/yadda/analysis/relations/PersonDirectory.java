package pl.edu.icm.yadda.analysis.relations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Directory of document authors.  Each person may have one or more contributions.
 * This directory allows to find the person given a contribution.
 * Contribution identifiers are constructed using {@link PersonDirectoryUtils}.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public interface PersonDirectory {
    /**
     * Returns the identifier of the person that made the given contribution.
     * 
     * @param contributionId The identifier of a contribution.
     * @return The identifier of the person that made the given contribution.
     */
    String getPerson(String contributionId);
    
    /**
     * Returns a list of all the roles in which the given person appeared.
     * 
     * @param personId A person.
     * @return A list of all the roles in which the given person appeared.
     */
    List<String> getRoles(String personId);
    
    /**
     * Returns the list of contributions of the given person in the given role.
     * 
     * @param personId A person.
     * @param role A role (possibly {@link null}).
     * @return The list of (identifiers of) contributions of the given person
     * in the given role.  If the role is {@link null}, the list of contributions
     * in all the roles.
     */
    List<String> getContributions(String personId, String role);

    /**
     * Returns the number of contributions of the given person in the given role.
     * 
     * @param personId A person.
     * @param role A role (possibly {@link null}).
     * @return The number of contribution of the given person in the given role.
     * If the role is {@link null}, the number of contributions in all the roles.
     */
    int getContributionsCount(String personId, String role);
    
    /**
     * Returns data about first associated contributor.
     * Data covers: Forenames, surname, title (if there is so) 
     * 
     * @param personId
     * @return
     * @author pdendek 
     */
    Map<String, String> getContributor(String personId);

	HashMap<String, Map<String, String>> getContributedItems(String personalityId,
			Map<String, String[]> attributeFilter,
			String[] order, boolean[] descendingOrder, long offset,
			int count);

	Map<String, Map<String, String>> searchPersonalities(Map<String, String[]> attributeFilter,
			String[] order, boolean[] descendingOrder, long offset, int count);
}
