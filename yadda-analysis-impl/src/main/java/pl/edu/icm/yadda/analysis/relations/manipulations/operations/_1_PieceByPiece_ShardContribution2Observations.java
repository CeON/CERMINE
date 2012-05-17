//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import org.openrdf.model.Resource;
//import org.openrdf.model.URI;
//import org.openrdf.model.ValueFactory;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.sail.nativerdf.NativeStore;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//import pl.edu.icm.yadda.analysis.relations.manipulations.YYYYYYYYYYYY.stattest.SesameBasicOperations;
//import pl.edu.icm.yadda.analysis.relations.manipulations.flow._1_Begining_SesameToCSVFlow;
//
//public class _1_PieceByPiece_ShardContribution2Observations extends SesameBasicOperations implements Operation{
//
//	@Override
//	public Object execute(Object repository, Map<String, Object> operationParam) {
//		
//		
//		
//    	try {
//    		Repository repo = (Repository) repository;
//    		
//    		this.repository = repo;
//    		
//    		HashMap<String,Set<String>> hm = getShards();
//
//    		Repository[] repositories = new Repository[6];
//    		for(int y = 0;y<6;y++){
//    			File dstFolder = new File(repo.getDataDir()+"_shard"+y);
//	    		dstFolder.mkdirs();
//	    		_1_Begining_SesameToCSVFlow.copyFolder(repo.getDataDir(), dstFolder);
//	    		NativeStore store=new NativeStore(dstFolder);	
//	    		SailRepository rep=new SailRepository(store);
//	    		rep.initialize();
//	    		repositories[y] = rep;
//    		}
//    		
//    		int observationNumber = 0;    		
//    		ValueFactory vf = null;
//    		
//    		for(Map.Entry<String, Set<String>> e : hm.entrySet()){
//    			Object[] arr = e.getValue().toArray();
//    			for(int i=0;i<arr.length;i++){
//    				String c1 = arr[i].toString();
//    				if(i%100==1) repo.getConnection().commit();
//    				for(int j=i+1;j<arr.length;j++){
//    					if(observationNumber%1000000==0){
//        					repo.getConnection().commit();
//        					repo=repositories[observationNumber/1000000];
//        					vf=repo.getValueFactory();
//        				}
//    					String c2 = arr[j].toString();
//    					URI observation = vf.createURI(RelConstants.NS_OBSERVATION+observationNumber);
//    					
//    					repo.getConnection().add(observation, vf.createURI(RelConstants.RL_OBSERVATION_ID), 
//    							vf.createLiteral(observationNumber),(Resource)null);
//    					repo.getConnection().add(observation, vf.createURI(RelConstants.RL_OBSERVATION_CONTRIBUTOR), 
//    							vf.createURI(c1),(Resource)null);
//    					repo.getConnection().add(observation, vf.createURI(RelConstants.RL_OBSERVATION_CONTRIBUTOR), 
//    							vf.createURI(c2),(Resource)null);
//    					observationNumber++;
//    				}
//    			}
//    		}
//			return repositories;	
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
