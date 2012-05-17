package pl.edu.icm.yadda.analysis.relations.manipulations.operations;

import java.util.Map;

import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;

/**
 * execute method removes from repository predicate which name is value from operationParam map
 * binded to "relationName" key.
 * 
 * It returns null in case of success and Exception object in case of failure
 * 
 * @author pdendek
 *
 */
public class DeletePredicate implements Operation {

	@Override
	public Object execute(Object repository, Map<String, Object> operationParam) {
		String relationName = (String) operationParam.get("relationName");
		Repository repo = (Repository) repository;
    	try {
    		repo.getConnection().remove((Resource)null, repo.getConnection().getValueFactory().createURI(relationName), (Resource)null,(Resource)null);
    	} catch (Exception e) {
			return e;
    	}
		return null;
	}

}
