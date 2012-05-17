package pl.edu.icm.yadda.analysis.relations.pj.clues;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.query.BindingSet;
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
public class PJEmailPrefixClue extends PJDisambiguator{

	private static final Logger log = LoggerFactory.getLogger(PJEmailPrefixClue.class);
		
	
	protected double checkIfSimilar(RepositoryConnection conn, String contribA, String contribB) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
				
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
		
	    
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
	    res = query.evaluate();
	    
	    List<String> emailsA = new LinkedList<String>();
	    aggregateRes(res, emailsA);
	    if(emailsA.size()==0)return 0;
	    
	    query = conn.prepareTupleQuery(QueryLanguage.SERQL, secondQueryString);
	    res = query.evaluate();
	    
	    List<String> emailsB = new LinkedList<String>();
	    aggregateRes(res, emailsB);
	    if(emailsB.size()==0)return 0;
	    
	    for(String outer : emailsA)
	    	for(String inner : emailsB)
	    		if(inner.equals(outer)) return 1;
	    	
	    return -1;
	}

	private void aggregateRes(TupleQueryResult res, List<String> emailPrefixes) throws QueryEvaluationException {
		while(res.hasNext()){
	    	BindingSet bs = res.next();
	    	
	    	String prefix = bs.getValue("email").stringValue().toLowerCase();
	    	try{
	    		prefix = prefix.substring(0, prefix.indexOf("@"));
	    	}catch(Exception e){
	    		try{
		    		prefix = prefix.substring(0, prefix.indexOf("®"));
		    	}catch(Exception e2){
		    		continue;
		    	}
	    	}
	    	
	    	emailPrefixes.add(prefix);
	    }
	    res.close();
	}

	@Override
	public String id() {
		return "email-proof";
	}
}
