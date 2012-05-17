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
//public class CountSthElseWithEmail {
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
//			System.out.println("====== KONTRYBUTORZY Z PEROSNAMI + EMAILEM ===========");
//			System.out.println("======================================================");
//			
//
//			String contribQuery = "" +
//			" Select distinct c,p,e" +
//			" from" +
//			" {c} <"+RelConstants.RL_IS_PERSON+"> {p}," +
//			" {c} <"+RelConstants.RL_CONTACT_EMAIL+"> {e}" +
//			" where" +
//			" p!=<http://yadda.icm.edu.pl/person#zbl#->" +
////			" and e!=\"\"" +
//			"";
//			
//			
//			TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			
//			int totalNumbr = 0;
//			int blank = 0;
//			while(res.hasNext()){
//				totalNumbr++;
//				BindingSet bs = res.next();
////				System.out.println(bs.getValue("e").stringValue());
//				if(bs.getValue("e").stringValue().length()==0){
//					blank++ ;
//				}
//				else{
////					System.out.println(bs.getValue("e").stringValue());
//				}
//			}		
//			System.out.println("All persones with ANY email are: "+totalNumbr);
//			System.out.println("All persones with BLANK email are: "+blank);
//			System.out.println("All persones with LONG email are: "+(totalNumbr-blank));
//		} catch (Exception e) {
//		}
//	}
//	
//	@Test
//	public void fc2o12() {
//		try {
//			System.out.println("======================================================");
//			System.out.println("============= KONTRYBUTORZY Z PERSONAMI===============");
//			System.out.println("======================================================");
//			
//			String contribQuery = "" +
//			" Select distinct c,p" +
//			" from" +
//			" {c} <"+RelConstants.RL_IS_PERSON+"> {p}" +
//			"";
//			
//			
//			TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			
//			int totalNumbr = 0;
//			int blank = 0;
//			while(res.hasNext()){
//				totalNumbr++;
//				BindingSet bs =  res.next();
//				bs.getValue("c").stringValue();
//				
//			}		
//			System.out.println("All persones with contrib: "+totalNumbr);
//		} catch (Exception e) {
//		}
//	}
//	
//	
//	@Test
//	public void fc2o1() {
//		try {
//			System.out.println("======================================================");
//			System.out.println("============= KONTRYBUTORZY Z EMAILEM ================");
//			System.out.println("======================================================");
//
//			String contribQuery = "" +
//			" Select distinct c,e" +
//			" from" +
//			" {c} <"+RelConstants.RL_CONTACT_EMAIL+"> {e}" +
//			"";
//			
//			
//			TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			
//			int totalNumbr = 0;
//			int blank = 0;
//			while(res.hasNext()){
//				totalNumbr++;
//				BindingSet bs = res.next();
////				System.out.println(bs.getValue("e").stringValue());
//				if(bs.getValue("e").stringValue().length()==0){
//					blank++ ;
//				}
//				else{
////					System.out.println(bs.getValue("e").stringValue());
//				}
//			}		
//			System.out.println("All persones with ANY email are: "+totalNumbr);
//			System.out.println("All persones with BLANK email are: "+blank);
//			System.out.println("All persones with LONG email are: "+(totalNumbr-blank));
//		} catch (Exception e) {
//		}
//	}
//	
//	
//	@Test
//	public void fc2o2() {
//		try {
////			System.out.println("======================================================");
////			System.out.println("============= KONTRYBUTORZY ==========================");
////			System.out.println("======================================================");
////
////			String contribQuery = "" +
////			" Select distinct c" +
////			" from" +
////			" {} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {c}" +
////			"";
////			
////			
////			TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
////			TupleQueryResult res = query.evaluate();
////			
////			int totalNumbr = 0;
////			int blank = 0;
//////			while(res.hasNext())
//////				totalNumbr++;
//////			
//////			System.out.println("All persones with ANY email are: "+totalNumbr);
//////			
////			System.out.println("All persones with ANY email are: "+totalNumbr);
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
