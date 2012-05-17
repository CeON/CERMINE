package pl.edu.icm.yadda.analysis.relations.pj.clues_occurence;

import java.util.HashSet;

import org.openrdf.model.Value;
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

import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;


public abstract class AggregateDisambiguator extends PJDisambiguator{

	protected static final Logger log = LoggerFactory.getLogger(AggregateDisambiguator.class);
		
	protected String queryString[] = new String[2];

	protected String askWho;
	
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

	protected abstract void initializeQuery(String c1, String c2);

	protected HashSet<String> count(TupleQueryResult res, String given) throws QueryEvaluationException {
		HashSet<String> emails = new HashSet<String>();
	    String prefix = null;
	    while(res.hasNext()){
	    	BindingSet bs = res.next();
	    	Value v = bs.getValue(given);
	    	prefix = v.stringValue();
	    	if(prefix!=null) prefix.toLowerCase();
	    	prefix = postprocess(prefix,emails);
	    }
	    res.close();
		return emails;
	}

	protected abstract String postprocess(String prefix, HashSet<String> emails);
}
