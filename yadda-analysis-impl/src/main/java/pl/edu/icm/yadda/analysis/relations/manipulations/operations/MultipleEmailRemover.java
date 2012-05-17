package pl.edu.icm.yadda.analysis.relations.manipulations.operations;

import java.util.Map;

import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;

/**
 * execute method removes from repository predicate which name is value from operationParam map
 * binded to "relationName" key.
 * 
 * It returns null in case of success and Exception object in case of failure
 * 
 * @author pdendek
 *
 */
public class MultipleEmailRemover implements Operation {

	@Override
	public Object execute(Object repository, Map<String, Object> operationParam) {
		Repository repo = (Repository) repository;
    	try {
    		ValueFactory vf = repo.getConnection().getValueFactory();
    		
    		String contribQuery = "" +
			" Select distinct c,p,e" +
			" from" +
			" {c} <"+RelConstants.RL_IS_PERSON+"> {p}," +
			" {c} <"+RelConstants.RL_CONTACT_EMAIL+"> {e}" +
			" where" +
			" e!=\"\"" +
			" AND p!=<http://yadda.icm.edu.pl/person#zbl#->" +
			"";
			
    		
    		TupleQuery query = repo.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
			TupleQueryResult res = query.evaluate();
			while(res.hasNext()){
				BindingSet bs = res.next();
				String prefix = bs.getValue("e").stringValue().toLowerCase();
				if(prefix.indexOf(",")!=-1){
					repo.getConnection().remove(vf.createURI(bs.getValue("c").stringValue()),
							vf.createURI(RelConstants.RL_CONTACT_EMAIL), 
							vf.createLiteral(bs.getValue("e").stringValue()),
							(Resource)null);
					
					String[] emails = prefix.split(",");
					for(String email : emails){
						email = email.trim();
						repo.getConnection().add(vf.createURI(bs.getValue("c").stringValue()),
								vf.createURI(RelConstants.RL_CONTACT_EMAIL), 
								vf.createLiteral(email),
								(Resource)null);
					}
				}
			}
    	} catch (Exception e) {
			return e;
    	}
		return null;
	}

}
