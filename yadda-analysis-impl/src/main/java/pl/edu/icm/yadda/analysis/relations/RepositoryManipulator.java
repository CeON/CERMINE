package pl.edu.icm.yadda.analysis.relations;

import org.openrdf.repository.Repository;

/**
 * 
 * @author pdendek
 * @deprecated
 */
public interface RepositoryManipulator {
    
	public Repository getRepository();
    public void setRepository(Repository repository);
    
    public void deleteRelation(String relationName);
    public void changeRelationName(String oldRelationName, String newRelationName);
    
}
