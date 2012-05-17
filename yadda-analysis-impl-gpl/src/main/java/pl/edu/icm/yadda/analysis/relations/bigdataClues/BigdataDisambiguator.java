package pl.edu.icm.yadda.analysis.relations.bigdataClues;

import java.util.HashSet;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.AggregateDisambiguator;

import com.bigdata.rdf.sail.BigdataSailRepository;

public abstract class BigdataDisambiguator extends AggregateDisambiguator {

	public Object clone(){
		try {
			BigdataDisambiguator prim = this.getClass().getConstructor((Class<?>[])null).newInstance();
			prim.askWho = this.askWho;
			prim.graphString = this.graphString;
			prim.queryString = this.queryString;
			return prim; 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * returns string which will ask sesame for all contributios following this clue
	 */
//	abstract String getWholeQuery();
	
	protected String graphString;
	
	protected static final Logger log = LoggerFactory
			.getLogger(BigdataDisambiguator.class);

	@Override
	@SuppressWarnings("unchecked")
	protected double checkIfSimilar(RepositoryConnection conn, String c1, String c2) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		initializeQuery(c1,c2);
		@SuppressWarnings("rawtypes")
		HashSet[] cooccurences = new HashSet[2]; 
		TupleQuery query = null;
		TupleQueryResult res = null;
		
	    for(int i=0; i<2;i++){
	    	query = conn.prepareTupleQuery(QueryLanguage.SERQL, queryString[i]);
		    res = query.evaluate();
		    cooccurences[i] = (HashSet<String>) count(res,askWho);
		    res.close();
		    if(cooccurences[i].size()==0)return -1;
	    }
		
	    
	    
	    cooccurences[0].retainAll(cooccurences[1]);
	    
	    int ret = cooccurences[0].size();
	    
	    cooccurences[0]=null;
	    cooccurences[1]=null;
	    cooccurences=null;
		query = null;
		res = null;
	    
	    return ret;
	}
	
	
	protected abstract void initializeGraph(String c1);
	
	public void copyTo(Repository destRep, List<String> cs) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {

		RepositoryConnection destConn = destRep.getConnection();
				
		for(String c : cs){
			initializeGraph(c);
			
			GraphQuery query = null;
			GraphQueryResult res = null;
			RepositoryConnection conn = getConn();
			query = conn.prepareGraphQuery(QueryLanguage.SERQL, graphString);
			res = query.evaluate();
			destConn.add(res, (Resource)null);
//			while(res.hasNext()){
//				
//			}
		    res.close();
		}
		destConn.close();
		conn.close();
	}

	@Override
	public RepositoryConnection getConn() {
		if (conn == null && repository == null)
			return null;
		try {
			if (conn == null || !conn.isOpen()) {
				if(repository instanceof BigdataSailRepository){
					BigdataSailRepository repo = (BigdataSailRepository) repository;
					conn = repo.getReadOnlyConnection();
					return conn;
				}else{
					conn = repository.getConnection();
					return conn;
				}
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
