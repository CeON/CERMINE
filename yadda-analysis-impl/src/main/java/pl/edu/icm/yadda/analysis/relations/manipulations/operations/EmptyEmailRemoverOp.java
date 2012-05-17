//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.Map;
//
//import org.openrdf.model.Resource;
//import org.openrdf.model.ValueFactory;
//import org.openrdf.query.BindingSet;
//import org.openrdf.query.QueryLanguage;
//import org.openrdf.query.TupleQuery;
//import org.openrdf.query.TupleQueryResult;
//import org.openrdf.repository.Repository;
//import org.slf4j.Logger;
//
//import pl.edu.icm.yadda.analysis.relations.auxil.XY;
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//
///**
// * execute method removes from repository predicate which name is value from operationParam map
// * binded to "relationName" key.
// * 
// * It returns null in case of success and Exception object in case of failure
// * 
// * @author pdendek
// *
// */
//public class EmptyEmailRemoverOp implements Operation {
//
//	@Override
//	public Object execute(Object repository, Map<String, Object> operationParam) {
//		Repository repo = (Repository) repository;
//    	try {
//    		ValueFactory vf = repo.getConnection().getValueFactory();
//    		
//    		String contribQuery = "" +
//			" Select distinct a,tag" +
//			" from" +
//			" {a} <"+RelConstants.RL_TAG+"> {tag}" +
//			"";
//			
//    		LinkedList<XY> ll = new LinkedList<XY>(); 
//    		TupleQuery query = repo.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			while(res.hasNext()){
//				BindingSet bs = res.next();
//				String art = bs.getValue("a").stringValue();
//				String tag = bs.getValue("tag").stringValue();
//				ll.add(new XY().setX(art).setY(tag));	
//			}
//			for(XY e : ll){
//				repo.getConnection().remove(vf.createURI(e.x),
//					vf.createURI(RelConstants.RL_TAG), 
//					vf.createLiteral(e.y),
//					(Resource)null);
//				repo.getConnection().remove(vf.createURI(e.x),
//					vf.createURI(RelConstants.RL_TAG), 
//					vf.createURI(e.y),
//					(Resource)null);
//			}
//    	} catch (Exception e) {
//			return e;
//    	}
//		return null;
//	}
//}
