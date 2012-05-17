package pl.edu.icm.yadda.analysis.relations.bigdataClues;

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

import com.bigdata.rdf.sail.BigdataSailRepository;


public class BigdataFeature8Year extends PJDisambiguator //implements CacheSupport
{

	protected static final Logger log = LoggerFactory
	.getLogger(BigdataDisambiguator.class);
	
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
	    try{
	    	if(res.hasNext()){
			    y1 = Integer.parseInt(res.next().getValue("year").toString().replace("\"", ""));
			    res.close();
			}else{
				res.close();
				return -1;
			}
	    }catch(Exception e){
	    	return -1;
	    }
		
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
	    res = query.evaluate();
	    
	    try{
	    	if(res.hasNext()){
				y2 = Integer.parseInt(res.next().getValue("year").toString().replace("\"", ""));
				res.close();
			}else{
				res.close();
				return -1;
			}
	    }catch(Exception e){
	    	return -1;
	    }
	    
		return Math.abs(y1-y2);
		
	}
	
	@Override
	public RepositoryConnection getConn() {
		if (conn == null && repository == null)
			return null;
		try {
			if (conn == null || !conn.isOpen()) {
				BigdataSailRepository repo = (BigdataSailRepository) repository;
				conn = repo.getReadOnlyConnection();
				return conn;
			}
		} catch (RepositoryException e) {
			log.error(e.toString());
			for (StackTraceElement s : e.getStackTrace())
				log.error(s.toString());
			return null;
		}
		return conn;
	}
}
