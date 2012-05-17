package pl.edu.icm.yadda.analysis.relations.pj.clues;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;


/**
 * Disambiguator granting contributors who contribute to the same journal
 * Result (similarity factor) is in {0,1} 
 *   
 * Contributor A and B contribute to journalA and journalB 
 * If either journalA or journalB is empty result is 0.
 * If both journals: A and B are equal result is 1.
 * 
 * @return similarity factor in {0,1}
 * 
 * @author pdendek
 *
 */
public class PJSameJournalClue extends PJDisambiguator{

	private static final Logger log = LoggerFactory.getLogger(PJSameJournalClue.class);
	

	@Override
	public String id() {
		return "same-journal-clue";
	}
	
	protected double checkIfSimilar(RepositoryConnection conn, String contribA, String contribB) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		
		String zeroQueryString = "" +
		  "Select distinct jour  \n" +
		  "from \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribA+">}, \n" +
		  "{doc} <"+RelConstants.RL_JOURNAL+"> {jour}, \n" +
		  "{doc2} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribB+">}, \n" +
		  "{doc2} <"+RelConstants.RL_JOURNAL+"> {jour} \n" +
		  "";

		TupleQuery query = null;
		TupleQueryResult res = null;
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    res = query.evaluate();
		if(res.hasNext()){
			res.close();
			return 1;
		}
		return 0; 
	}
}
