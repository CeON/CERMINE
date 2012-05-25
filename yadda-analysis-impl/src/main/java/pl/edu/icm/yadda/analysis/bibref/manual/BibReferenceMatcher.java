package pl.edu.icm.yadda.analysis.bibref.manual;

import java.util.Set;

import pl.edu.icm.yadda.tools.bibref.model.BibReferenceTriple;

/**
 * Match bibliographic references of given NLM.
 * 
 * @see BibReferenceTriple
 * @author Krzyś Wojciechowski
 * 
 */
public interface BibReferenceMatcher {

    /**
     * Matches documents referenced form given document.
     * 
     * @param nlm
     *            NLM of document references we want to match
     * @return matched references
     * @throws Exception
     */
    Set<BibReferenceTriple> matchBibReferencedIds(String nlm) throws Exception;

    /**
     * Matches documents referencing given document. Implementation should
     * return null if implemented as stub.
     * 
     * @param nlm
     *            NLM of document references we want to match
     * @return matched references
     * @throws Exception
     */
    Set<BibReferenceTriple> matchBibReferencingIds(String nlm) throws Exception;

}
