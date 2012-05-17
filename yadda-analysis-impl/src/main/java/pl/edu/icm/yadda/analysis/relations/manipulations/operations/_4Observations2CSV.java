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
//import org.openrdf.repository.RepositoryConnection;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//
//public class _4Observations2CSV implements Operation{
//
//	@Override
//	public Object execute(Object repository, Map<String, Object> operationParam) {
//		Repository repo = (Repository) repository;
//		
//		Integer featureNum = (Integer) operationParam.get("featureNum"); 
//		String csvFilePath = (String) operationParam.get("csvFilePath");
//		File parent = new File(csvFilePath);
//		parent.mkdirs();
//		
//		File outFile = new File(parent, "_" + System.nanoTime()+".csv");
//		
//    	try {
//    		RepositoryConnection con = repo.getConnection();
//    		con.setAutoCommit(false);
//    		
//    		FileWriter fw = new FileWriter(outFile);
//    		
//    		fw.write("id ");
//    		for(int i=0;i<featureNum;i++){
//    			fw.write("f"+i+" ");	
//    		}
//    		fw.write("samePerson\n");
//    		fw.flush();
//    		
//    		StringBuilder sb = new StringBuilder(" Select ");
//    		
//    		for(int i=0; i<featureNum;i++) sb.append("f"+i+", ");
//    		
//    		sb.append(" same, oid \n  from ");
//    		
//    		for(int i=0; i<featureNum;i++) sb.append(" {o} <"+RelConstants.RL_OBSERVATION_FEATURE+i+"> {f"+i+"}, \n");
//    		
//    		
//    		sb.append(" {o} <"+RelConstants.RL_OBSERVATION_ID+"> {oid}, \n");
//    		sb.append(" {o} <"+RelConstants.RL_OBSERVATION_CONTAINS_SAME_PERSON+"> {same}");
//    		
//    		String contribQuery = sb.toString();
//			
//    		TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
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
//					System.out.println(iii+"/289000"+"\t"+(iii*100/(double)250000)+"%\t"+new Date());
//				}
//				iii++;
//				
//				String[] fval = new String[featureNum];
//				for(int i=0; i<featureNum;i++){
//					fval[i] = bs.getValue("f"+i).stringValue();
//				}
//				String same = bs.getValue("same").stringValue();
//				 
//				fw.write(oid+" ");
//				for(int i=0;i<fval.length;i++){
//					fw.write(fval[i]+" ");
//				}
//				fw.write(same+"\n");
//				fw.flush();
//			}
//			res.close();
//			con.close();
//			con=null;
//			return null;	
//    	} catch (Exception e) {
//			return e;
//    	}
//	}
//}
