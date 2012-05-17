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
 * Disambiguator granting contributors who are connected by reference.
 * Result (similarity factor) is {1,0} 
 * 
 * Let's have contributor A and B.
 * 1) 'A' and 'B' have the same name and surname.
 * 2) 'A' put is reference article of 'B'.
 * 
 * If above are satisfied Disambiguator conclude that they are the same
 * and give return 1. Otherwise it return 0. 
 * 
 * Both sequences (A is referring to B, B is referring to A)
 * are checked.
 * 
 * @return similarity factor in {1,0}
 * 
 * @author pdendek
 *
 */
public class PJReferenceToSameNameProof extends PJDisambiguator{

	private static final Logger log = LoggerFactory.getLogger(PJReferenceToSameNameProof.class);

	@Override
	public String id() {
		return "reference-proof";
	}
	
	
	public double checkIfSimilar(RepositoryConnection conn, String contribA, String contribB) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
	
		
		//check if contribA is referring to contribB which are the same in matter of names
		String zeroQueryString = "" +
		  "Select distinct document  \n" +
		  "from \n" +
		  "{<"+contribA+">} <"+RelConstants.RL_FORENAMES+"> {fnames_org}, \n" +
		  "{<"+contribA+">} <"+RelConstants.RL_SURNAME+"> {snames_org}, \n" +
		  "{document} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribA+">},  \n" +
		  "{document} <"+RelConstants.RL_REFERENCES+"> {ref},  \n" +
		  "{ref} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribB+">},  \n" +
		  "{<"+contribB+">} <"+RelConstants.RL_FORENAMES+"> {fnames_org}, \n" +
		  "{<"+contribB+">} <"+RelConstants.RL_SURNAME+"> {snames_org}" +
		  "";
		
		//same in reverse direction
		String firstQueryString = "" +
		  "Select distinct document  \n" +
		  "from \n" +
		  "{<"+contribB+">} <"+RelConstants.RL_FORENAMES+"> {fnames_org}, \n" +
		  "{<"+contribB+">} <"+RelConstants.RL_SURNAME+"> {snames_org}, \n" +
		  "{document} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribB+">},  \n" +
		  "{document} <"+RelConstants.RL_REFERENCES+"> {ref},  \n" +
		  "{ref} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribA+">},  \n" +
		  "{<"+contribA+">} <"+RelConstants.RL_FORENAMES+"> {fnames_org}, \n" +
		  "{<"+contribA+">} <"+RelConstants.RL_SURNAME+"> {snames_org}" +
		  "";
		
		
		TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    TupleQueryResult res = query.evaluate();
	    
	    if(res.hasNext()){
	    	res.close();
	    	return 1;
	    }else{
	    	res.close();
	    	res = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString).evaluate();
	    	if(res.hasNext()){
	    		res.close();
		    	return 1;
		    }else{
		    	res.close();
		    	return 0;
		    }
	    }
	}
}
