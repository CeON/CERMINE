package pl.edu.icm.yadda.analysis.relations;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;

/**
 * A dummy disambiguator which always returns zero.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class DummyDisambiguator extends PJDisambiguator {

    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.Disambiguator#id()
     */
    @Override
    public String id() {
        return "dummy";
    }

	@Override
	protected double checkIfSimilar(RepositoryConnection conn,
			String contributionIdA, String contributionIdB)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		return 0.0;
	}
}
