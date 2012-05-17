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
 * Disambiguator granting contributors who contribute in time distance less then 70 years.
 * Result (similarity factor) is in {-1,0,1} 
 *   
 * Contributor A and B contribute in yearA and yearB.
 * If one of those years is unknown result is 0.
 * If both are known, and difference between them is higher then 70years result is -1
 * If difference is lower then 70years result is 1  
 * 
 * @return similarity factor in {-1,0,1}
 * @update @return similarity factor in {-1,0,0.01}
 * 
 * @author pdendek
 *
 */
public class PJStrictLessThen70YearsClue extends PJDisambiguator{

	private static final Logger log = LoggerFactory.getLogger(PJStrictLessThen70YearsClue.class);	
	
	@Override
	public String id() {
		return "less-then-70-years-strict";
	}
	
	
	protected double checkIfSimilar(RepositoryConnection conn, String contribA, String contribB) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		
		String zeroQueryString = "" +
		  "Select distinct year  \n" +
		  "from \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribA+">}, \n" +
		  "{doc} <"+RelConstants.RL_YEAR+"> {year} \n" +
		  "";
		
		String firstQueryString = "" +
		  "Select distinct year  \n" +
		  "from \n" +
		  "{doc2} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribB+">}, \n" +
		  "{doc} <"+RelConstants.RL_YEAR+"> {year} \n" +
		  "";

		Integer y1 = null, 
				y2 = null;
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    res = query.evaluate();
		if(res.hasNext()){
			y1 = Integer.parseInt(res.next().getValue("year").toString().replaceAll("\"", ""));
			res.close();
		}else{
			res.close();
			return 0;
		}
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
	    res = query.evaluate();
		if(res.hasNext()){
			y2 = Integer.parseInt(res.next().getValue("year").toString().replaceAll("\"", ""));
			res.close();
		}else{
			res.close();
			return 0;
		}

		if(Math.abs(y1-y2)>70) return -1;
		else return 0.01;
	}
}
