/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.yadda.analysis.relations.pj.trace;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
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
 * (zmodyfikowany przez pdendek, 2011-02-16)
 * (zmodyfikowany przez pdendek, 2011-03-01)
 * (zmodyfikowany przez pdendek, 2011-03-06)
 * 
 * @author kura
 */
public class KuraForenameSurnameTrace {
	
	private static final Logger log = LoggerFactory.getLogger(KuraForenameSurnameTrace.class);
	
    /* Sparql 1
     * select ?x ?y ?z
    where
    {
    ?x  <http://yadda.icm.edu.pl/yadda#has-surname> ?z .
    ?y  <http://yadda.icm.edu.pl/yadda#has-surname> ?z .
    ?x  <http://yadda.icm.edu.pl/yadda#has-forenames> ?nx .
    ?y  <http://yadda.icm.edu.pl/yadda#has-forenames> ?ny
    FILTER (  ?x != ?y && STR( ?x) < STR ( ?y)  && ( regex(?nx , ?ny ) || regex(?ny , ?nx )  ))
    }
     */

    static String nameURI = "http://yadda.icm.edu.pl/yadda#trace-forenames";
    static String surnameURI = "http://yadda.icm.edu.pl/yadda#trace-surname";

    public static String findPersonForContributor(RepositoryConnection conn, String contributor) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
    	
        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, "select distinct  ?name ?surname \n"
            + " where {"
            + "<"+contributor +">"+ " <"+RelConstants.RL_FORENAMES+"> ?name ."
            + "<"+contributor +">"+ "<"+RelConstants.RL_SURNAME+"> ?surname "
            + "}");
        String name = null;
        String surname = null;
        TupleQueryResult res = query.evaluate();
        try {
            if (res.hasNext()) {
                BindingSet ds = res.next();
                name = ds.getValue("name").stringValue();
                surname = ds.getValue("surname").stringValue();

            }

        } finally {
            res.close();
        }

        if (surname == null) {
            // nie ma persony bez nazwiska
            return null;
        }

        if(name!=null) name=name.replace("\"", "").replace(" ", "");
        surname=surname.replace("\"", "").replace(" ", "");
        
        String ask = "select distinct  ?id \n"
            + " where {"
            + "?id  <http://yadda.icm.edu.pl/yadda#forenames> \"" + name + "\" . "
            + " ?id <http://yadda.icm.edu.pl/yadda#surname> \"" + surname + "\""
            + "}";
        String id = null;        
        
        try{
        	 id = inner(conn, contributor, name, surname, ask, id); 
        }catch(Exception e){
        	log.error("Error querry:\n"+ask);
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
        }
        if (id==null) id=addPerson(conn, name, surname);
		 ValueFactory f = conn.getValueFactory();
		 conn.add(f.createURI(contributor), f.createURI(RelConstants.RL_IS_PERSON), f.createURI(id));
		 return id;
    }

	private static String inner(RepositoryConnection conn, String contributor,
			String name, String surname, String ask, String id)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		TupleQuery query;
		TupleQueryResult res;
		query = conn.prepareTupleQuery(QueryLanguage.SPARQL, ask);
		res = query.evaluate();
		try {
		    if (res.hasNext()) {
		        BindingSet ds = res.next();
		        id = ds.getValue("id").stringValue();
		    }
		} finally {
		    res.close();
		}    
		if (id==null && name.length()>0){
			
		    StringBuilder sb = new StringBuilder();
		    query = conn.prepareTupleQuery(QueryLanguage.SPARQL, "select distinct  ?id \n"
		            + " where {"
		            + "?id  <http://yadda.icm.edu.pl/yadda#forenames> \"" + name.substring(0, 1) + "\" . "
		            + " ?id <http://yadda.icm.edu.pl/yadda#surname> \"" + surname + "\""
		            + "}");
		    res = query.evaluate();

		    try {
		        if (res.hasNext()) {
		            BindingSet ds = res.next();
		            id = ds.getValue("id").stringValue();
   	
		        }
   	
		    } finally {
		        res.close();
		    }
		 }

		 if (id==null && name.length()>0){
		 	query = conn.prepareTupleQuery(QueryLanguage.SPARQL, "select distinct  ?id \n"
		             + " where {"
		             + "?id  <http://yadda.icm.edu.pl/yadda#forenames> \"" + name.substring(0, 1) + ".\" . "
		             + " ?id <http://yadda.icm.edu.pl/yadda#surname> \"" + surname + "\""
		             + "}");
		    res = query.evaluate();
   	
		    try {
		        if (res.hasNext()) {
		            BindingSet ds = res.next();
		            id = ds.getValue("id").stringValue();
		        }
		    } finally {
		        res.close();
		    }
		 }
		 return id;
	}

    /**
     * assuming ther is no propare person for this contributor
     * @param conn
     * @return
     */
    public static String addPerson(RepositoryConnection conn, String name, String surName) throws RepositoryException {
        String traceId = generateTraceId(name+surName);
        URI uriTraceId = conn.getValueFactory().createURI(traceId);
        ValueFactory f = conn.getValueFactory();
        conn.add(uriTraceId, f.createURI(nameURI), f.createLiteral(name));
        conn.add(uriTraceId, f.createURI(surnameURI), f.createLiteral(surName));

        return traceId;
    }

    public static void checkSingleContributor(RepositoryConnection conn, String contributor) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
//        log.info("Checking contributor "+contributor);
        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, "select distinct  ?z \n"
            + " where { \n"
            + "<"+contributor+">" + " <"+RelConstants.RL_IS_PERSON+"> ?z \n "
            + "}");
        TupleQueryResult res = query.evaluate();
        try {
           if (!res.hasNext()) {
               findPersonForContributor(conn, contributor);
               }
           else{
              String per=res.next().getValue("z").stringValue().trim();
               
               if ( per.isEmpty()||per.equals(RelConstants.NS_PERSON) ) {
                 // to jeszcze usunięcie starej relacji 
                 findPersonForContributor(conn, contributor);
            }
           }

        } finally {
            res.close();
        }
    }

    /**
     * Regular, incremental analyze
     * 
     * @param conn
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public static void analyze(RepositoryConnection conn) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SERQL, "select distinct x \n"
            + " where \n"
            + "{x} <"+RelConstants.RL_SURNAME+"> {} \n"
            + ",[{x} <"+RelConstants.RL_IS_PERSON+"> trace] \n"
            + "where \n"
            + "trace=null \n"
            + "");

        TupleQueryResult res = query.evaluate();
        try {
            while (res.hasNext()) 
            	checkSingleContributor(conn, res.next().getValue("x").stringValue());
        } finally {
            res.close();
        }
    }

    /**
     * Starts from removing old traces 
     * 
     * @param conn
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public static void burnAnalyze(RepositoryConnection conn) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
    	conn.remove((Resource)null, conn.getValueFactory().createURI(RelConstants.RL_IS_PERSON), (Resource)null,(Resource)null);
    	
    	//get all surnames
    	String generalQueryString = "select distinct y \n"
        + " from \n"
        + "{x} <"+RelConstants.RL_SURNAME+"> {y} \n"
        + "";

//    	System.out.println(generalQueryString);
        TupleQuery generalQuery = conn.prepareTupleQuery(QueryLanguage.SERQL, generalQueryString);
        
        TupleQueryResult generalResult = generalQuery.evaluate();
        
        try {
            while (generalResult.hasNext()){
            		//get all contributors with same surname
            		String trace = UUID.randomUUID().toString();
            		String preciseQueryString = 
            			"select distinct x \n"
                        + " from \n"
                        + "{x} <"+RelConstants.RL_SURNAME+"> {\""+generalResult.next().getValue("y").stringValue()+"\"} \n"
                        + ""; 
            		TupleQuery preciseQuery = conn.prepareTupleQuery(QueryLanguage.SERQL, preciseQueryString);
//            		System.out.println(preciseQueryString);
            		TupleQueryResult preciseResult = preciseQuery.evaluate();
            		
            		//add same trace to contribs with same surname
            		while (preciseResult.hasNext()){
            			String name = preciseResult.next().getValue("x").toString();
//            			System.out.println(name);
            			conn.add(conn.getValueFactory().createURI(name),
            					conn.getValueFactory().createURI(RelConstants.RL_IS_PERSON),
            					conn.getValueFactory().createURI(RelConstants.NS_PERSON+trace),
            					(Resource)null);
            		}
            		preciseResult.close();
            }
        } finally {
            generalResult.close();
        }
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
    public static String generateTraceId(String contribution) {
        try {
        	String ret = RelConstants.NS_PERSON + UUID.nameUUIDFromBytes(contribution.getBytes("UTF-8")).toString();
        	if(ret.equals(RelConstants.NS_PERSON))
        		throw new IllegalStateException("Trace number not generated");
            return  ret;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Cannot happen", e);
        }
    }
}
