package pl.edu.icm.yadda.analysis.relations.pj.auxil;

import java.util.Random;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import pl.edu.icm.yadda.analysis.relations.Disambiguator;

public abstract class PJDisambiguator implements Disambiguator {

	private static final Logger log = LoggerFactory.getLogger(PJDisambiguator.class);
	protected RepositoryConnection conn;
	protected static Random rnd = new Random();

	protected Repository repository;
	
	public void closeRepository(){
		if(repository==null){ conn=null; return;}
		try{ if(conn!=null) conn.close(); }
		catch(RepositoryException e){}
		conn=null;
		repository=null;
	}
	
	public void closeAndRemoveConnection(){
		try{ if(conn!=null) conn.close(); }
		catch(RepositoryException e){}
		conn=null;
	}
	
	public Repository getRepository() {
        return repository;
    }

    @Required
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
	
	protected abstract double checkIfSimilar(RepositoryConnection conn, 
			String contributionIdA, String contributionIdB)
		throws RepositoryException, MalformedQueryException, QueryEvaluationException;

	@Override
	public double analyze(String contributionIdA, String contributionIdB){ 
		try {
			RepositoryConnection conn = getConn();
			double result = checkIfSimilar(conn, contributionIdA,contributionIdB);
			conn.close();
			return result;
		} catch (RepositoryException e) {
			log.error(e.toString());
//			log.error(Arrays.toString(e.getStackTrace()));			
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		} catch (MalformedQueryException e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		} catch (QueryEvaluationException e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		} catch (Exception e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		}
		return 0;
	}
	
	public RepositoryConnection getConn(){
		if(conn==null && repository == null) return null;
		try {
			if(conn==null || !conn.isOpen()){
				conn=repository.getConnection();
				return conn;
			}
		} catch (RepositoryException e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
			return null;
		}
		return conn;
	}	
}
