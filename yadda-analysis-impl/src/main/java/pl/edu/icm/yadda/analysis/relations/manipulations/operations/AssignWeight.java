//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.util.Map;
//
//import org.openrdf.model.Resource;
//import org.openrdf.model.URI;
//import org.openrdf.model.ValueFactory;
//import org.openrdf.query.BindingSet;
//import org.openrdf.query.QueryLanguage;
//import org.openrdf.query.TupleQuery;
//import org.openrdf.query.TupleQueryResult;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryConnection;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//
//public class AssignWeight implements Operation {
//
//	@Override
//	public Object execute(Object repository, Map<String, Object> operationParam) {
//		try {
//			Repository repo = (Repository) repository;
//			RepositoryConnection con = repo.getConnection();
//			ValueFactory vf = con.getValueFactory();
//			String initWeight = (String) operationParam.get("initWeight");
//			
//			String contribQuery = "" +
//			  " Select distinct observ \n " +
//			  " from \n " +
//			  " {observ} <"+RelConstants.RL_OBSERVATION_ID+"> {} \n " +
//			  "";
//			
//			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			while(res.hasNext()){
//				BindingSet bs = res.next();		
//				
//				URI id = vf.createURI(bs.getValue("observ").toString());
//				URI pred = vf.createURI(RelConstants.RL_OBSERVATION_HAS_WEIGHT);
//				
//				repo.getConnection().remove(id, pred, null, (Resource)null);
//				repo.getConnection().add(id,pred,
//							vf.createLiteral(initWeight),(Resource)null);
//			}
//
//			res.close();
//			return null;	
//		} catch (Exception e) {
//			return e;
//		}
//	}
//
//}
