//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import org.openrdf.model.Resource;
//import org.openrdf.model.ValueFactory;
//import org.openrdf.query.BindingSet;
//import org.openrdf.query.QueryLanguage;
//import org.openrdf.query.TupleQuery;
//import org.openrdf.query.TupleQueryResult;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryConnection;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;
//
//public class _3ObservationsFeatureChecker implements Operation{
//
//	@Override
//	public Object execute(Object repository, Map<String, Object> operationParam) {
//		Repository repo = (Repository) repository;
//		
//		List<PJDisambiguator> pjdislist = (List<PJDisambiguator>) operationParam.get("disambiguatorList"); 
//		
//		int more=0;
//		
//    	try {
//    		RepositoryConnection con = repo.getConnection();
//    		con.setAutoCommit(false);
//    		
//    		ValueFactory vf = con.getValueFactory();
//    		
//    		String contribQuery = "" +
//			" Select o, oid, c1, c2" +
//			" from" +
//			" {o} <"+RelConstants.RL_OBSERVATION_ID+"> {oid}, " +
//			" {o} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c1}, " +
//			" {o} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c2} " +
//			" WHERE " +
//			" c1!=c2 " +
//			"";
//			
//    		
//    		TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			LinkedList<String> obsList = new LinkedList<String>();
//			int iii=0;
//			while(res.hasNext()){
//				BindingSet bs = res.next();
//				String o = bs.getValue("o").stringValue();
//				String oid = bs.getValue("oid").stringValue();
//				
//				if(obsList.contains(oid)) continue;
//				else obsList.add(oid);
//				
//				iii++;
//				if(iii%1000==0){
//					System.out.println("Wykonano" + iii +"\t"+new Date());
//				}
//				
//				String c1 = bs.getValue("c1").stringValue();
//				String c2 = bs.getValue("c2").stringValue();
//				int fid =0;
//				
//				for(PJDisambiguator d : pjdislist){
//					d.setRepository(repo);
//					
//					double db = d.analyze(c1, c2);
//					if (db>0){
//						more++;
//					}
//					
//					con.add(vf.createURI(o), 
//							vf.createURI(RelConstants.RL_OBSERVATION_FEATURE+fid),
//							vf.createLiteral(db),
//							(Resource)null);
//					fid++;
//				}
//			}
//			System.out.println(pjdislist.get(0).toString()+" wystapila "+more+" razy.");
//			
//			res.close();
//			con.commit();
//			con.close();
//			con=null;
//			return null;	
//    	} catch (Exception e) {
//			return e;
//    	}
//	}
//}
