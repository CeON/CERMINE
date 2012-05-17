//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Map;
//
//import org.openrdf.query.BindingSet;
//import org.openrdf.query.QueryLanguage;
//import org.openrdf.query.TupleQuery;
//import org.openrdf.query.TupleQueryResult;
//import org.openrdf.repository.Repository;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//
//public class _4aPArtOfObservations2CSV implements Operation{
//
//	@Override
//	public Object execute(Object repository, Map<String, Object> operationParam) {
//		Repository repo = (Repository) repository;
//		
//		String featureText = (String) operationParam.get("featureText"); 
//		String csvFilePath = (String) operationParam.get("csvFilePath");
//		
//    	try {
//    		FileWriter fw = new FileWriter(new File(csvFilePath));
//    		
//    		fw.write("id f\n");
//    		fw.flush();
//    		
//    		String s = " Select x, oid \n  " +
//    			" from " +
//    			" {o} <"+featureText+"> {x}, \n" +
//    			" {o} <"+RelConstants.RL_OBSERVATION_ID+"> {oid}" +
//    			"";
//    		
//    		String contribQuery = s.toString();
//			
//    		TupleQuery query = repo.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			
//			HashSet<String> done = new HashSet<String>(); 
//			
//			int iii = 0;
//			while(res.hasNext()){
//				BindingSet bs = res.next();
//				String oid = bs.getValue("oid").stringValue();
//				
//				if(done.contains(oid)) continue;
//				else done.add(oid);
//				
//				if(iii%1000==0){
//					System.out.println(iii+"/289000"+"\t"+(iii*100/(double)289000)+"%\t"+new Date());
//				}
//				iii++;
//				
//				String x = bs.getValue("x").stringValue();
//				 
//				fw.write(oid+" "+x+"\n");
//				fw.flush();
//			}
//			res.close();
//			return null;	
//    	} catch (Exception e) {
//			return e;
//    	}
//	}
//}
