//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.util.LinkedList;
//import java.util.Map;
//
//import org.openrdf.model.Resource;
//import org.openrdf.model.URI;
//import org.openrdf.model.ValueFactory;
//import org.openrdf.query.QueryLanguage;
//import org.openrdf.query.TupleQuery;
//import org.openrdf.query.TupleQueryResult;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryConnection;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//
//public class Contributions2Observations implements Operation {
//
//	@Override
//	public Object execute(Object repository, Map<String, Object> operationParam) {
//		try {
//			Repository repo = (Repository) repository;
//			RepositoryConnection con = repo.getConnection();
//			ValueFactory vf = con.getValueFactory();
//			
//			
//			String contribQuery = "" +
//			  "Select distinct contrib  \n" +
//			  "from \n" +
//			  "{contrib} <"+RelConstants.RL_SURNAME+"> {} \n" +
//			  "";
//			
//			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			LinkedList<String> tempAllContribList = new LinkedList<String>();
//			while(res.hasNext())
//				tempAllContribList.add(res.next().getValue("contrib").toString());
//			res.close();
//		
//			int observationNumber = 0;
//			for(int i=0;i<tempAllContribList.size();i++)
//				for(int j=i+1;j<tempAllContribList.size();j++){
//					String contribA = tempAllContribList.get(i);
//					String contribB = tempAllContribList.get(j);
//
//					URI observation = vf.createURI(RelConstants.NS_OBSERVATION+observationNumber);
//					
//					repo.getConnection().add(observation, vf.createURI(RelConstants.RL_OBSERVATION_ID), 
//							vf.createLiteral(observationNumber),(Resource)null);
//					repo.getConnection().add(observation, vf.createURI(RelConstants.RL_OBSERVATION_CONTRIBUTOR), 
//							vf.createURI(contribA),(Resource)null);
//					repo.getConnection().add(observation, vf.createURI(RelConstants.RL_OBSERVATION_CONTRIBUTOR), 
//							vf.createURI(contribB),(Resource)null);
//					observationNumber++;
//				}
//			
//			return null;	
//		} catch (Exception e) {
//			return e;
//		}
//	}
//}
