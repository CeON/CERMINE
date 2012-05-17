//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.io.File;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.openrdf.model.Resource;
//import org.openrdf.model.Statement;
//import org.openrdf.model.URI;
//import org.openrdf.model.ValueFactory;
//import org.openrdf.query.BindingSet;
//import org.openrdf.query.QueryLanguage;
//import org.openrdf.query.TupleQuery;
//import org.openrdf.query.TupleQueryResult;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryConnection;
//import org.openrdf.repository.RepositoryException;
//import org.openrdf.repository.RepositoryResult;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.rio.RDFFormat;
//import org.openrdf.sail.memory.MemoryStore;
//import org.openrdf.sail.nativerdf.NativeStore;
//
//import pl.edu.icm.yadda.analysis.relations.RepositoryManipulator;
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
//import pl.edu.icm.yadda.tools.reparser.Node;
//import pl.edu.icm.yadda.tools.reparser.RegexpParser;
//
//public class CountEmailPersones {
//
//	SesameManipulator c2o_m;
//	SesameManipulator fc2o_m;
//	String filePath1 = "pl/edu/icm/yadda/analysis/relations/manipulations/operations/test1.N3";
////	String filePath2 = "/home/pdendek/sample/TEST_R/NATIVE_REPO_V1";
//	String filePath2 = "/home/pdendek/sample/repo_v4";
//	Repository repository; 
//	
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@Before
//	public void setUp() throws Exception {
//		NativeStore store=new NativeStore(new File(filePath2));		
//		SailRepository rep=new SailRepository(store);
//		rep.initialize();
//		repository=rep;		
//		fc2o_m = new SesameManipulator(repository, new FingerprintContributions2Observations());
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@After
//	public void tearDown() throws Exception {
//		repository.shutDown();
//	}
//
////	@Test
//	public void voidTest() {
//	}
//	
//	@Test
//	public void fc2o() {
//		try {
//			System.out.println("======================================================");
//			System.out.println("=====================FC2O=============================");
//			System.out.println("======================================================");
//
//			
//			String contribQuery = "" +
//			" Select distinct c,p,e,l,y,j" +
//			" from" +
//			" {c} <"+RelConstants.RL_IS_PERSON+"> {p}," +
//			" {a} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {c}," +
//			" {a} <"+RelConstants.RL_YEAR+"> {y}," +
//			" {a} <"+RelConstants.RL_LANGUAGE+"> {l}," +
//			" {a} <"+RelConstants.RL_JOURNAL+"> {j}, \n" +
//			" {a} <"+RelConstants.RL_TAG+"> {t}, \n" +
//			" {t} <"+RelConstants.RL_TEXT+"> {text}, \n" +
//			" {c} <"+RelConstants.RL_CONTACT_EMAIL+"> {e}" +
//			" where" +
//			" e!=\"\"" +
//			" AND y!=\"\"" +
//			"";
//			
//			
//			
//			HashMap<String, HashMap<String,Integer>> emailhm = new HashMap<String, HashMap<String,Integer>>();
//			HashMap<String, Set<String>> taghm = new HashMap<String, Set<String>>();
//			HashMap<String,Integer> langhm = new HashMap<String,Integer>();
//			HashMap<String,Integer> yearhm = new HashMap<String,Integer>();
//			HashMap<String,Integer> journalhm = new HashMap<String,Integer>();
//			
//			TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			
//			int totalNumbr = 0;
//			while(res.hasNext()){
//				totalNumbr++;
//				BindingSet bs = res.next();
//				
//				if(journalhm.containsKey(bs.getValue("j").stringValue())){
//					Integer value = journalhm.get(bs.getValue("j").stringValue());
//					value++;
//					journalhm.put(bs.getValue("j").stringValue(), value);
//				}else{
//					journalhm.put(bs.getValue("j").stringValue(), 1);
//				}
//				
//				if(langhm.containsKey(bs.getValue("l").stringValue())){
//					Integer value = langhm.get(bs.getValue("l").stringValue());
//					value++;
//					langhm.put(bs.getValue("l").stringValue(), value);
//				}else{
//					langhm.put(bs.getValue("l").stringValue(), 1);
//				}
//				
//				if(yearhm.containsKey(bs.getValue("y").stringValue())){
//					Integer value = yearhm.get(bs.getValue("y").stringValue());
//					value++;
//					yearhm.put(bs.getValue("y").stringValue(), value);
//				}else{
//					yearhm.put(bs.getValue("y").stringValue(), 1);
//				}
//				
//				
//				
//				if(taghm.containsKey(bs.getValue("p").stringValue())){
//					taghm.get(bs.getValue("p").stringValue()).add(bs.getValue("t").stringValue());
//				}else{
//					HashSet<String> inner = new HashSet<String>();
//					inner.add(bs.getValue("t").stringValue());
//					taghm.put(bs.getValue("p").stringValue(), inner);
//				}
//				
//				
//				
//				if(emailhm.containsKey(bs.getValue("p").stringValue())){
//					HashMap<String,Integer> inner = emailhm.get(bs.getValue("p").stringValue());
//					if(inner.containsKey(bs.getValue("e").stringValue())){
//						Integer value = inner.get(bs.getValue("e").stringValue());
//						value++;
//						inner.put(bs.getValue("e").stringValue(), value);
//					}
//				}else{
//					HashMap<String,Integer> inner = new HashMap<String,Integer>();
//					inner.put(bs.getValue("e").stringValue(), 1);
//					emailhm.put(bs.getValue("p").stringValue(), inner);
//				}
//			}
//			
//			System.out.println("All persones with email are: "+totalNumbr);
//			for(Map.Entry<String, HashMap<String,Integer>>  e : emailhm.entrySet() ){
//				if(e.getValue().entrySet().size()>1){
//					System.out.println("For person "+e.getKey()+" we have following e-mails:");
//					for(Map.Entry<String, Integer> inner : e.getValue().entrySet()){
//						System.out.println("\t"+inner.getKey()+" occured "+inner.getValue()+" times");
//					}
//				}
//			}
//			
//			System.out.println("All langs used are: ");
//			for(Map.Entry<String, Integer>  e : langhm.entrySet() ){
//				System.out.println("["+e.getValue()+"]\t"+e.getKey());
//			}
//			
//			System.out.println("All journals used are: ");
//			for(Map.Entry<String, Integer>  e : journalhm.entrySet() ){
//				System.out.println("["+e.getValue()+"]\t"+e.getKey());
//			}
//			
//			System.out.println("All years used are: ");
//			for(Map.Entry<String, Integer>  e : yearhm.entrySet() ){
//				System.out.println("["+e.getValue()+"]\t"+e.getKey());
//			}
//			
//			System.out.println("All tags used are: ");
//			for(Map.Entry<String, Set<String>>  e : taghm.entrySet() ){
//				System.out.println("["+e.getKey()+"]\t"+e.getValue());
//			}
//			
//		} catch (Exception e) {
//		}
//	}
//	
//	public static void printRepository(Repository nr) throws RepositoryException{
//		RepositoryResult<Statement> rr = nr.getConnection().getStatements((Resource)null, null, null, false, (Resource)null);
//    	List<Statement> rl = rr.asList();
//    	for(Statement s : rl){
//    		System.out.println(
//    				s.getSubject().toString()+"   ----(   "+
//    				s.getPredicate().toString()+"   )--->   "+
//    				s.getObject().toString());
//    	}
//	}
//}
