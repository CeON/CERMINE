package pl.edu.icm.yadda.analysis.relations.manipulations.operations;

import java.util.LinkedList;
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import pl.edu.icm.yadda.analysis.relations.auxil.XY;

public class RenamePrediacate implements Operation{

	@Override
	public Object execute(Object repository, Map<String, Object> operationParam) {
			Repository repo = (Repository) repository;
			String oldRelationName = (String) operationParam.get("oldRelationName");
			String newRelationName = (String) operationParam.get("newRelationName");
			
			try{
				RepositoryConnection conn = repo.getConnection();
				conn.setAutoCommit(false);
				
				executeOnURIObject(oldRelationName,newRelationName, conn);
				conn.commit();
				
				executeOnLiteralObject(oldRelationName,newRelationName, conn);
				conn.commit();
				
				conn.remove((Resource)null, conn.getValueFactory().createURI(oldRelationName), null, (Resource)null);
				
				conn.commit();
				conn.close();
				return null;
			} catch (Exception e) {
				return e;
			}
			
	}

	private Object executeOnLiteralObject(String oldRelationName,
			String newRelationName, RepositoryConnection conn) throws Exception {
		LinkedList<XY> list = new LinkedList<XY>();
    	
		String queryString = "" +
		  "Select distinct x,y  \n" +
		  "from \n" +
		  "{x} <"+oldRelationName+"> {y} \n" +
		  "where isLiteral(y) \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
			ValueFactory vf = conn.getValueFactory();
	    	
	    	URI po = vf.createURI(oldRelationName);
	    	URI pn = vf.createURI(newRelationName);
			
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, queryString);
			res = query.evaluate();
		    while(res.hasNext()){
		    	BindingSet bs = res.next();
		    	list.add(
		    			new XY().
		    			setX(bs.getValue("x").toString()).
		    			setY(bs.getValue("y").toString())
		    			);
		    }
		    res.close();
		    
		    for(XY xy : list){
		    	URI x = vf.createURI(xy.x);
		    	Literal y = vf.createLiteral(xy.y);
		    	conn.add(x, pn, y,(Resource)null);
		    }
		    list.clear();
			return null;
	}

	private Object executeOnURIObject(String oldRelationName,
			String newRelationName, RepositoryConnection conn) throws Exception {
		LinkedList<XY> list = new LinkedList<XY>();
    	
		String queryString = "" +
		  "Select distinct x,y  \n" +
		  "from \n" +
		  "{x} <"+oldRelationName+"> {y} \n" +
		  "where isURI(y) \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
			ValueFactory vf = conn.getValueFactory();
	    	
	    	URI po = vf.createURI(oldRelationName);
	    	URI pn = vf.createURI(newRelationName);
			
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, queryString);
			res = query.evaluate();
		    while(res.hasNext()){
		    	BindingSet bs = res.next();
		    	list.add(
		    			new XY().
		    			setX(bs.getValue("x").toString()).
		    			setY(bs.getValue("y").toString())
		    			);
		    }
		    res.close();
		    
		    for(XY xy : list){
		    	URI x = vf.createURI(xy.x);
		    	URI y = vf.createURI(xy.y);
		    	conn.add(x, pn, y,(Resource)null);
		    }
		    list.clear();
			return null;
	}
}

