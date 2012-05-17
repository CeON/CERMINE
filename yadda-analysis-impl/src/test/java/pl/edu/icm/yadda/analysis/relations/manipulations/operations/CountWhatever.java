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
//import org.openrdf.model.Value;
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
//public class CountWhatever {
//
//	SesameManipulator c2o_m;
//	SesameManipulator fc2o_m;
//	SesameManipulator mer_m;
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
//		mer_m = new SesameManipulator(repository, new MultipleEmailRemover());
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
//	@Test
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
//			mer_m.execute(null);
//			
//			String contribQuery = "" +
//			" Select distinct c,p,e" +
//			" from" +
//			" {c} <"+RelConstants.RL_IS_PERSON+"> {p}," +
//			" {c} <"+RelConstants.RL_CONTACT_EMAIL+"> {e}" +
//			" where" +
//			" e!=\"\"" +
//			" AND p!=<http://yadda.icm.edu.pl/person#zbl#->" +
//			"";
//			
//			
//			
//			
//			
//			HashMap<String, HashMap<String,Integer>> emailCompleteHm = new HashMap<String, HashMap<String,Integer>>();
//			HashMap<String, HashMap<String,Integer>> emailPrefixHm = new HashMap<String, HashMap<String,Integer>>();
//			
//			HashMap<String,Integer> langhm = new HashMap<String,Integer>();
//			HashMap<String,Integer> yearhm = new HashMap<String,Integer>();
//			HashMap<String,Integer> journalhm = new HashMap<String,Integer>();
//			
//			 Set<String> uniqueemails = new HashSet<String>();
//			 Set<String> uniqueprefix = new HashSet<String>();
//			
//			TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			
//			int totalNumbr = 0;
//			while(res.hasNext()){
//				totalNumbr++;
//				BindingSet bs = res.next();
//				
//				String prefix = bs.getValue("e").stringValue().toLowerCase();
//				
//				uniqueemails.add(prefix);
//				
//				if(emailCompleteHm.containsKey(bs.getValue("p").stringValue())){
//					HashMap<String,Integer> inner = emailCompleteHm.get(bs.getValue("p").stringValue());
//					if(inner.containsKey(prefix)){
//						Integer value = inner.get(prefix);
//						value++;
//						inner.put(prefix, value);
//					}else{
//						inner.put(prefix, 1);
//					}
//				}else{
//					HashMap<String,Integer> inner = new HashMap<String,Integer>();
//					inner.put(prefix, 1);
//					emailCompleteHm.put(bs.getValue("p").stringValue(), inner);
//				}
//				
//				try{
//					prefix = prefix.substring(0, prefix.indexOf("@"));
//					uniqueprefix.add(prefix);
//				}catch (Exception e) {
//					try{
//						prefix = prefix.substring(0, prefix.indexOf("®"));
//						uniqueprefix.add(prefix);
//					}catch (Exception e2) {
//						System.out.println(prefix);
//						e2.printStackTrace();
//					}
//				}
//				
//				if(emailPrefixHm.containsKey(bs.getValue("p").stringValue())){
//					HashMap<String,Integer> inner = emailPrefixHm.get(bs.getValue("p").stringValue());
//					if(inner.containsKey(prefix)){
//						Integer value = inner.get(prefix);
//						value++;
//						inner.put(prefix, value);
//					}else{
//						inner.put(prefix, 1);
//					}
//				}else{
//					HashMap<String,Integer> inner = new HashMap<String,Integer>();
//					inner.put(prefix, 1);
//					emailPrefixHm.put(bs.getValue("p").stringValue(), inner);
//				}
//			}
//			
//			System.out.println("All persones with email are: "+totalNumbr);
//			
//			System.out.println("======================================================");
//			System.out.println("=====================FC2O=============================");
//			System.out.println("======================================================");		
//			
//			int moreThenOne =0;
//			for(Map.Entry<String, HashMap<String,Integer>>  e : emailCompleteHm.entrySet() ){
//				if(e.getValue().entrySet().size()>1){
//					String p = e.getKey();
//					int q = 0;
//					System.out.println("For person "+p+" we have following e-mails:");
//					for(Map.Entry<String, Integer> inner : e.getValue().entrySet()){
//						System.out.println("["+inner.getValue()+"]\t"+inner.getKey());
//						if(inner.getValue()>1) moreThenOne+=inner.getValue();
//					}
//				}
//			}
//			System.out.println("No of reused emails is: "+moreThenOne);
//			
//			System.out.println("======================================================");
//			System.out.println("=====================FC2O=============================");
//			System.out.println("======================================================");
//			
//			int moreThenOnePrefix =0;
//			for(Map.Entry<String, HashMap<String,Integer>>  e : emailPrefixHm.entrySet() ){
//				if(e.getValue().entrySet().size()>1){
//					String p = e.getKey();
//					int q = 0;
//					System.out.println("For person "+p+" we have following e-mails:");
//					for(Map.Entry<String, Integer> inner : e.getValue().entrySet()){
//						System.out.println("["+inner.getValue()+"]\t"+inner.getKey());
//						if(inner.getValue()>1) moreThenOnePrefix+=inner.getValue();
//					}
//				}
//			}
//			System.out.println("No of reused emails is: "+moreThenOnePrefix);
//			
//			System.out.println("No of unique emails: "+uniqueemails.size());
//			System.out.println("No of unique email prefixes: "+uniqueprefix.size());
////			
////			int howManyInfix = 0;
////			outer: for(String outer : uniqueprefix)
////				if(outer.indexOf(".")!=-1 || outer.indexOf("-")!=-1)
////					inner: for(String inner : uniqueprefix){
////						if(!outer.equals(inner)){
////							String[] innerParts =  inner.split("\\.|-");
////							String[] outerParts =  outer.split("\\.|-");
////							
////					for(String ipart : innerParts)
////								for(String opart : outerParts){
////									if(ipart.equals(opart)){
////										System.out.println(inner);
////										System.out.println(outer);
////										System.out.println(ipart);
////										System.out.println(opart);
////										System.out.println("===============");
////										howManyInfix++;
////										continue inner;
////									}
////								}
////						}
////					}
////			System.out.println("No of email infixies is: "+howManyInfix);
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
