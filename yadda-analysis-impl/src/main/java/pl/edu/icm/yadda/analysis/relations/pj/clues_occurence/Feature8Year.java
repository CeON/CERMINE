package pl.edu.icm.yadda.analysis.relations.pj.clues_occurence;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;


public class Feature8Year extends PJDisambiguator{

	@Override
	public String id() {
		return "year-clue";
	}

	@Override
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
			return -1;
		}
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
	    res = query.evaluate();
		if(res.hasNext()){
			y2 = Integer.parseInt(res.next().getValue("year").toString().replace("\"", ""));
			System.out.println(Integer.toString(y2));
			res.close();
		}else{
			res.close();
			return -1;
		}
		return Math.abs(y1-y2);
		
	}
}
