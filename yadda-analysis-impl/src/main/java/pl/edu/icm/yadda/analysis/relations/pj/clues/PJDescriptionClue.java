package pl.edu.icm.yadda.analysis.relations.pj.clues;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;

/**
 * not implemented!!!
 * 
 * @author pdendek
 *
 */
public class PJDescriptionClue extends PJDisambiguator{


	private static final Logger log = LoggerFactory.getLogger(PJDescriptionClue.class);
		
	
	@Override
	public String id() {
		// TODO Implement me!
		throw new UnsupportedOperationException();
	}

	@Override
	protected double checkIfSimilar(RepositoryConnection conn,
			String contributionIdA, String contributionIdB)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		// TODO Auto-generated method stub
		return 0;
	}
}
