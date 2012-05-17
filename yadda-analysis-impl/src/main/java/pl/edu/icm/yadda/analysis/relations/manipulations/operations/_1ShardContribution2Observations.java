//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//
//import org.openrdf.model.Resource;
//import org.openrdf.model.URI;
//import org.openrdf.model.ValueFactory;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryConnection;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//import pl.edu.icm.yadda.analysis.relations.manipulations.YYYYYYYYYYYY.stattest.SesameBasicOperations;
//
//public class _1ShardContribution2Observations extends SesameBasicOperations implements Operation{
//
//	@Override
//	public Object execute(Object repository, Map<String, Object> operationParam) {
//		
//		Random random = new Random();
//		
//    	try {
//    		Repository repo = (Repository) repository;
//    		this.repository = repo;
//    		
//    		HashMap<String,Set<String>> hm = getShards();
//    		ValueFactory vf = repo.getValueFactory();
//    		
//    		RepositoryConnection conn = repo.getConnection(); 
//    		
//    		conn.setAutoCommit(false);
//    		
//    		int observationNumber = 0;
//    		int iii = 0;
//    		for(Map.Entry<String, Set<String>> e : hm.entrySet()){
//    			Object[] arr = e.getValue().toArray();
//    			iii++;
//    			
//    			System.out.println("Proceeded Surname: "+iii+"/"+hm.entrySet().size()+"\tsize:: "+e.getValue().size()+"\tobservationsProducedTillNow:"+observationNumber+"\t"+new Date());
//    			
//    			for(int i=0;i<arr.length;i++){
//    				String c1 = arr[i].toString();
//    				for(int j=i+1;j<arr.length;j++){
//    					String c2 = arr[j].toString();
//    					URI observation = vf.createURI(RelConstants.NS_OBSERVATION+observationNumber);
//    					
//    					conn.add(observation, vf.createURI(RelConstants.RL_OBSERVATION_ID), 
//    							vf.createLiteral(observationNumber),(Resource)null);
//    					conn.add(observation, vf.createURI(RelConstants.RL_OBSERVATION_CONTRIBUTOR), 
//    							vf.createURI(c1),(Resource)null);
//    					conn.add(observation, vf.createURI(RelConstants.RL_OBSERVATION_CONTRIBUTOR), 
//    							vf.createURI(c2),(Resource)null);
//    					observationNumber++;
//    				}
//    			}
//    		}
//			conn.commit();
//			conn.close();
//			conn=null;
//			return null;	
//    	} catch (Exception e) {
//			return e;
//    	}
//	}
//
//	
//	
//	@Override
//	public void test() {
//	}
//}
