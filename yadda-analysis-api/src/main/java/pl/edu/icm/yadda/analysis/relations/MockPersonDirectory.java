package pl.edu.icm.yadda.analysis.relations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock implementation of person directory.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class MockPersonDirectory implements PersonDirectory {
    private static final String ROLE = "oth";

    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.PersonDirectory#getPerson(java.lang.String)
     */
    @Override
    public String getPerson(String contributionId) {
        return contributionId;
    }

    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.PersonDirectory#getRoles(java.lang.String)
     */
    @Override
    public List<String> getRoles(String personId) {
        return Arrays.asList(ROLE);
    }

    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.PersonDirectory#getContributions(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getContributions(String personId, String role) {
        if (role == null || ROLE.equals(role)) {
            return Arrays.asList(personId);
        } else {
            return Collections.emptyList();
        }
    }

    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.PersonDirectory#getContributionsCount(java.lang.String, java.lang.String)
     */
    @Override
    public int getContributionsCount(String personId, String role) {
        return getContributions(personId, role).size();
    }

    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.PersonDirectory#getContributor(java.lang.String)
     * @author pdendek
     */
	@Override
	public Map<String, String> getContributor(String personId) {
		// TODO Auto-generated method stub
		return new HashMap<String, String>();
	}

	@Override
	public HashMap<String, Map<String, String>> getContributedItems(
			String personalityId, Map<String, String[]> attributeFilter,
			String[] order, boolean[] descendingOrder, long offset, int count) {
		// TODO Auto-generated method stub
		return new HashMap<String,Map<String,String>>();
	}

	@Override
	public Map<String, Map<String, String>> searchPersonalities(
			Map<String, String[]> attributeFilter, String[] order,
			boolean[] descendingOrder, long offset, int count) {
		// TODO Auto-generated method stub
		return new HashMap<String,Map<String,String>>();
	}
}
