package pl.edu.icm.yadda.analysis.relations;

import java.util.List;

import pl.edu.icm.yadda.analysis.AnalysisException;

/**
 * Read-write access to person directory.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public interface PersonDirectoryBackend {

    /**
     * Returns identifiers of groups of contributions in the directory.
     * A group may contain contributions from many persons,
     * but all the contributions of a person should ideally reside
     * in the same group.
     * 
     * @return Identifiers of groups of contributions in the directory.
     * @throws AnalysisException
     */
    Iterable<String> groupIds() throws AnalysisException;
    
    /**
     * Returns contributions within a given group in the directory.
     * 
     * @param groupId Group identifiers.
     * @return Identifiers of contributions within the group.
     * @throws AnalysisException
     */
    List<String> members(String groupId) throws AnalysisException;
    
    /**
     * Stores a person in the directory.
     * 
     * @param personId Person identifier.
     * @param contributionId List of contribution identifiers by the person.
     * @throws AnalysisException
     */
    void storePerson(String personId, Iterable<String> contributionId) throws AnalysisException;
    
    Object getRepository();
    void setRepository(Object repo);
}
