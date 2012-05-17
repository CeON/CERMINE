package pl.edu.icm.yadda.analysis.relations;

/**
 * A heuristic for determining whether two contributions come from the same person.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public interface Disambiguator {

    /**
     * Returns a unique identifier of the disambiguator.
     * @return A unique identifier of the disambiguator.
     */
    String id();
    
    /**
     * Analyzes two contributions and assesses whether they were made by the same person or not.
     * 
     * @param contributionIdA Contribution identifier.
     * @param contributionIdB Contribution identifier.
     * @return A real number from the range [-1.0, 1.0], where -1.0 means "definitely different persons",
     * 1.0 means "definitely the same person".  When no data supporting either way is present, 0.0 is returned.
     */
    double analyze(String contributionIdA, String contributionIdB);
}
