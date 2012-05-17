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
 * Result (similarity factor) is in {-1} and [0,1] 
 *   
 * Contributor A and B contribute in yearA and yearB.
 * If one of those years is unknown result is 0.
 * If both are known, and difference between them is higher then 70years result is -1
 * If difference is lower then 70years similarity factor is calculated from following equation:
 * 1- (y1-y2)^2/(70^2) 
 * 
 * @return similarity factor in {-1} and [0,1]
 * 
 * @author pdendek
 *
 */
public class PJNotStrictLessThen70YearsClue extends PJDisambiguator{

	private static final Logger log = LoggerFactory.getLogger(PJNotStrictLessThen70YearsClue.class);

	
	@Override
	public String id() {
		return "less-then-70-years-clue-not-strict";
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
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribB+">}, \n" +
		  "{doc} <"+RelConstants.RL_YEAR+"> {year} \n" +
		  "";

		Integer y1 = null, 
				y2 = null;
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    res = query.evaluate();
	    //can't be null
		if(res.hasNext()){
		    y1 = Integer.parseInt(res.next().getValue("year").toString().replace("\"", ""));
		    System.out.println(Integer.toString(y1));
		    res.close();
		}else{
			res.close();
			return 0;
		}
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
	    res = query.evaluate();
		if(res.hasNext()){
			y2 = Integer.parseInt(res.next().getValue("year").toString().replace("\"", ""));
			System.out.println(Integer.toString(y2));
			res.close();
		}else{
			res.close();
			return 0;
		}

		System.out.println("y1: "+Integer.toString(y1)+ " y2: "+Integer.toString(y2));
		
		if(Math.abs(y1-y2)>70) return -1;
		return (1-
				  (y1-y2)*(y1-y2)
				  /
				  (4900.0)
			    );
	}
}
