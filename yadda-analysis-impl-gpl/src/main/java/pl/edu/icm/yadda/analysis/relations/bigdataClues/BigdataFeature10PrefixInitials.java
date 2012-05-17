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


public class BigdataFeature10PrefixInitials extends PJDisambiguator 
//implements CacheSupport{
{

	protected static final Logger log = LoggerFactory
	.getLogger(BigdataDisambiguator.class);
	
	@Override
	public String id() {
		return "prefix-initials-clue";
	}

	@Override
	protected double checkIfSimilar(RepositoryConnection conn, String contribA, String contribB) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		
		String zeroQueryString = "" +
		  "Select distinct year  \n" +
		  "from \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribA+">}, \n" +
		  "{doc} <"+RelConstants.RL_INITIALS+"> {year} \n" +
		  "";
		
		String firstQueryString = "" +
		  "Select distinct year  \n" +
		  "from \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribB+">}, \n" +
		  "{doc} <"+RelConstants.RL_INITIALS+"> {year} \n" +
		  "";

		String i1 = null, 
				i2 = null;
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    res = query.evaluate();
	    //can't be null
		if(res.hasNext()){
		    i1 = res.next().getValue("year").stringValue();
		    res.close();
		}else{
			res.close();
			return -1;
		}
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
	    res = query.evaluate();
		if(res.hasNext()){
			i2 = res.next().getValue("year").stringValue();
			res.close();
		}else{
			res.close();
			return -1;
		}
		
		if(i1.equals(i2)) return 1;
		
		byte[] i1b = i1.getBytes();
		byte[] i2b = i2.getBytes();
		
		int i = 0;
		for(;i<i1b.length && i<i2b.length;i++){
			if(i1b[i]!=i2b[i]) break;
		}
		return i;
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
