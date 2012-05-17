/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.yadda.analysis.relations.pj.trace;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.openrdf.model.ValueFactory;
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
import org.springframework.beans.factory.annotation.Required;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;

/**
 * 
 * @author pdendek
 */
public class PJOnlySurnameTrace {
	
	private static final Logger log = LoggerFactory.getLogger(PJOnlySurnameTrace.class);
    
    public static void analyze(RepositoryConnection conn) throws RepositoryException, MalformedQueryException, QueryEvaluationException {

    	
		String zeroQueryString = "" +
		  "Select distinct name  \n" +
		  "from \n" +
		  "{} <"+RelConstants.RL_NAME+"> {name} \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		String name = null;
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    res = query.evaluate();
	    if(res.hasNext()){
	    	name = res.next().getValue("name").stringValue();
	    	takeAllFromOneName(conn, name);
	    }
	    res.close();
		return;
    }


	private static void takeAllFromOneName(RepositoryConnection conn,
			String name) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		LinkedList<String> contribList = new LinkedList<String>();
		
		String zeroQueryString = "" +
		  "Select distinct contrib, person  \n" +
		  "from \n" +
		  "{contrib} <"+RelConstants.RL_NAME+"> {<"+name+">}, \n" +
		  "[{contrib} <"+RelConstants.RL_IS_PERSON+"> {person}] \n" +
		  "where \n" +
		  "person = NULL \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    res = query.evaluate();
	    while(res.hasNext()){
	    	BindingSet bs = res.next();
	    	name = bs.getValue("person").stringValue();
	    	if(name != null) continue;
	    	name = bs.getValue("contrib").stringValue();
	    	if(name == null) continue;
	    	contribList.add(name);
	    }
	    res.close();
	    String id = generatePersonId(contribList);
	   
	    try {
            ValueFactory vf = conn.getValueFactory();
            for (String contrib : contribList) {
                conn.add(vf.createURI(contrib), vf.createURI(RelConstants.RL_IS_PERSON), vf.createURI(id));
            }
            conn.commit();
        } catch (RepositoryException e) {
            log.error(e.toString());
        }
		return;
	}
	private RepositoryConnection conn;
	
	@Required
	public void setRepositoryConnection(RepositoryConnection conn){
		this.conn=conn;
	}
	
	public RepositoryConnection getRepositoryConnection(){
		return conn;
	}
    

	/**
     * Generates a trace identifier based on a given list of contributions.
     * 
     * @param contributionIds
     *            List of contributions by a given person.
     * @return Trace identifier.
     */
    public static String generatePersonId(List<String> contributionIds) {
        Collections.sort(contributionIds);
        StringBuilder builder = new StringBuilder();
        for (String contributionId : contributionIds) {
            builder.append(contributionId).append('\n');
        }
        try {
        	String ret = RelConstants.NS_PERSON + UUID.nameUUIDFromBytes(builder.toString().getBytes("UTF-8")).toString();
        	if(ret.equals(RelConstants.NS_PERSON))
        		throw new IllegalStateException("Trace number not generated");
            return  ret;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Cannot happen", e);
        }
    }
}
