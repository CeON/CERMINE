package pl.edu.icm.yadda.analysis.relations.pj.proofs;

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
 * Disambiguator granting contributors who have same email address. 
 * Result (similarity factor) is {-1,0,1}
 * 
 * If contributors A and B have same email Disambiguator return 1.
 * If one or both contributors don't have email Disambiguator return 0.
 * If both contributors have email and emails are different Disambiguator return -1.
 * 
 * @return 1 if emails are the same; 
 * 0 if one or two of contributors doesn't have email; 
 * -1 if both contributors have email, but they are not the same 
 * 
 * @author pdendek
 *
 */
public class PJEmailProof extends PJDisambiguator{

	private static final Logger log = LoggerFactory.getLogger(PJEmailProof.class);
		
	
	protected double checkIfSimilar(RepositoryConnection conn, String contribA, String contribB) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		String zeroQueryString = "" +
		  "Select distinct email  \n" +
		  "from \n" +
		  "{<"+contribA+">} <"+RelConstants.RL_CONTACT_EMAIL+"> {email}, \n" +
		  "{<"+contribB+">} <"+RelConstants.RL_CONTACT_EMAIL+"> {email} \n" +
		  "";
		
		String firstQueryString = "" +
		  "Select distinct email  \n" +
		  "from \n" +
		  "{<"+contribA+">} <"+RelConstants.RL_CONTACT_EMAIL+"> {email} \n" +
		  "";
		
		String secondQueryString = "" +
		  "Select distinct email  \n" +
		  "from \n" +
		  "{<"+contribB+">} <"+RelConstants.RL_CONTACT_EMAIL+"> {email} \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    res = query.evaluate();
	    if(res.hasNext()){
	    	res.close();
	    	return 1;
	    }
	    res.close();
	    
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
	    res = query.evaluate();
	    
	    int emailA=0;
	    while(res.hasNext()){
	    	res.next();
	    	emailA++;
	    }
	    res.close();
	    if(emailA==0)return 0;
	    
	    query = conn.prepareTupleQuery(QueryLanguage.SERQL, secondQueryString);
	    res = query.evaluate();
	    
	    int emailB=0;
	    while(res.hasNext()){
	    	res.next();
	    	emailB++;
	    }
	    res.close();
	    if(emailB==0)return 0;
	    
	    return -1;
	}

	@Override
	public String id() {
		return "email-proof";
	}
}
