package pl.edu.icm.yadda.analysis.relations.pj.backend;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.ValueFactory;
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
import org.springframework.beans.factory.annotation.Required;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryBackend;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryCreator;

public class PJPersonDirectoryBackend implements PersonDirectoryBackend {

	private static final Logger log = LoggerFactory.getLogger(PersonDirectoryCreator.class);
	private RepositoryConnection conn;
	
	@Required
	public void setRepositoryConnection(Repository repo){
		try {
			this.conn=repo.getConnection();
		} catch (RepositoryException e) {
			log.error(e.toString());
		}
	}
	
	public void setRepositoryConnection(RepositoryConnection conn){
		this.conn=conn;
	}
	
	public RepositoryConnection getRepositoryConnection(){
		return conn;
	}	
	
	private String groupIdTrace;

	@Required
	void setGroupIdTrace(String groupIdTrace){
		this.groupIdTrace = groupIdTrace;
	}
	
	String getGroupIdTrace(){
		return groupIdTrace;
	}	
	
	private String personPredicate;
	
	String getPersonPredicate(){
		return personPredicate;
	}
	
	@Required
	void setPersonPredicate(String personPredicate){
		this.personPredicate = personPredicate;
	}	
	
	@Override
	public Iterable<String> groupIds() throws AnalysisException {
		LinkedList<String> groupsIds = new LinkedList<String>(); 
		
		//check if contribA is referring to contribB which are the same in matter of names
		String zeroQueryString = "" +
		  "Select distinct person  \n" +
		  "from \n" +
		  "{} <"+getPersonPredicate()+"> {person} \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		try {
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
			res = query.evaluate();
			while(res.hasNext()){
		    	groupsIds.add(res.next().getValue("person").toString());
		    }
		} catch (RepositoryException e) {
			throw new AnalysisException(e);
		} catch (MalformedQueryException e) {
			throw new AnalysisException(e);
		} catch (QueryEvaluationException e) {
			throw new AnalysisException(e);
		}
		return groupsIds;
	}

	@Override
	public List<String> members(String groupId) throws AnalysisException {
		LinkedList<String> members = new LinkedList<String>(); 
		
		//check if contribA is referring to contribB which are the same in matter of names
		String zeroQueryString = "" +
		  "Select distinct member  \n" +
		  "from \n" +
		  "{member} <"+getGroupIdTrace()+"> {"+groupId+"} \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		try {
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
			res = query.evaluate();
			while(res.hasNext()){
		    	members.add(res.next().getValue("member").toString());
		    }
		} catch (RepositoryException e) {
			throw new AnalysisException(e);
		} catch (MalformedQueryException e) {
			throw new AnalysisException(e);
		} catch (QueryEvaluationException e) {
			throw new AnalysisException(e);
		}
		return members;
	}

	@Override
	public void storePerson(String personId, Iterable<String> contributionId)
			throws AnalysisException {
		
		ValueFactory vf = conn.getValueFactory();
		
		for(String contrib : contributionId){
			try {
				conn.add(vf.createURI(contrib),vf.createURI(getPersonPredicate()),vf.createURI(personId));
			} catch (RepositoryException e) {
				throw new AnalysisException(e);
			}			
		}
	}
	
	public Object clone(){
		PJPersonDirectoryBackend pdb = new PJPersonDirectoryBackend();
		pdb.setGroupIdTrace(this.groupIdTrace);
		pdb.setPersonPredicate(this.personPredicate);
		pdb.setRepository(null);
		return pdb;
	}

	@Override
	public Object getRepository() {
		return conn.getRepository();
	}

	@Override
	public void setRepository(Object repo) {
		if(repo==null) this.conn=null;
		if(!(repo instanceof Repository)) throw new ClassCastException();
		try {
			this.conn=((Repository)repo).getConnection();
		} catch (RepositoryException e) {
			// be silent like a ninja
		}	
	}
}
