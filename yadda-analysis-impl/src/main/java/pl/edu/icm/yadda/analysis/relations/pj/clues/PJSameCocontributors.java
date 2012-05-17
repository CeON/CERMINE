package pl.edu.icm.yadda.analysis.relations.pj.clues;

import java.util.LinkedList;
import java.util.List;

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
 * not implemented!!!
 * 
 * @author pdendek
 *
 */
public class PJSameCocontributors extends PJDisambiguator{


	private static final Logger log = LoggerFactory.getLogger(PJSameCocontributors.class);
		
	
	@Override
	public String id() {
		return "same-co-contributor";
	}

	protected double checkIfSimilar(RepositoryConnection conn, String contribA, String contribB) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		
		String zeroQueryString = "" +
		  "Select distinct sname  \n" +
		  "from \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribA+">}, \n" +
		  "{cA2} <"+RelConstants.RL_SURNAME+"> {sname}, \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {cA2} \n" +
		  " where " +
		  " <"+contribA+">!= cA2" +
		  "";
		
		String firstQueryString = "" +
		"Select distinct sname  \n" +
		  "from \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribB+">}, \n" +
		  "{cB2} <"+RelConstants.RL_SURNAME+"> {sname}, \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {cB2} \n" +
		  " where " +
		  " <"+contribB+">!= cB2" +
		  "";

		
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    res = query.evaluate();
	    //can't be null
	    List<String> cocontribA = new LinkedList<String>(); 
		while(res.hasNext()){
		    cocontribA.add(res.next().getValue("sname").toString());
		}
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
	    res = query.evaluate();
	    List<String> cocontribB = new LinkedList<String>(); 
		while(res.hasNext()){
		    cocontribB.add(res.next().getValue("sname").toString());
		}

		int match = 0;
		for(String outer : cocontribA)
			for(String inner : cocontribA){
				if(outer.equals(inner)) match++;
			}
		
		return match;		
	}
}
