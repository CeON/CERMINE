package pl.edu.icm.yadda.analysis.relations;

import java.util.List;

import org.openrdf.repository.Repository;

/**
 * 
 * @author pdendek
 * @deprecated
 */
public interface AccuracyRepositoryChecker {
	
	public Repository getRepository();
    public void setRepository(Repository repository);
	
	public void printAccuracyStats();
	public List<Object> sameOccurenceCheck(String trueRelationName,String testedRelationName) ;
}
