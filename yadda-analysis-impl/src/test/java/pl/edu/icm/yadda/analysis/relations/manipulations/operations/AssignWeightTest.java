//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.io.File;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.openrdf.model.Resource;
//import org.openrdf.model.Statement;
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
//
//import org.openrdf.model.URI;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
//
//public class AssignWeightTest{
//
//	SesameManipulator fc2o_m;
//	SesameManipulator aw_m;
//	String filePath1 = "pl/edu/icm/yadda/analysis/relations/manipulations/operations/test1.N3";
//	
//	Repository repository; 
//	
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@Before
//	public void setUp() throws Exception {
//		URL repoURL = this.getClass().getClassLoader().getResource(filePath1);
//		File repoFile = new File(repoURL.toURI());
//
//    	MemoryStore store=new MemoryStore();
//		SailRepository rep=new SailRepository(store);
//		rep.initialize();
//		rep.getConnection().add(repoFile, null, RDFFormat.N3, (Resource)null);
//		
//		repository=rep;		
//		fc2o_m = new SesameManipulator(repository, new FingerprintContributions2Observations());
//		aw_m = new SesameManipulator(repository, new AssignWeight());
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
////	@Test
//	public void wa1() {
//		try {
//			System.out.println("======================================================");
//			System.out.println("=====================WA1==============================");
//			System.out.println("======================================================");
//
//			fc2o_m.execute(null);
//			HashMap<String,Object> hm = new HashMap<String,Object>();
//			hm.put("initWeight", "1");
//			aw_m.execute(hm);
//			
//			
//			String contribQuery = "" +
//			  "Select distinct c,i,w  \n" +
//			  "from \n" +
//			  "{c} <"+RelConstants.RL_OBSERVATION_ID+"> {i}, \n" +
//			  "{c} <"+RelConstants.RL_OBSERVATION_HAS_WEIGHT+"> {w} \n" +
//			  "";
//			
//			TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			
//			while(res.hasNext()){
//				BindingSet bs = res.next();
//				System.out.println("Observation("+bs.getValue("i")+") has weight "+bs.getValue("w"));
//			}
//			
//		} catch (Exception e) {
//		}
//	}
//
////	@Test
//	public void wa123() {
//		
//			System.out.println("======================================================");
//			System.out.println("=====================WA123============================");
//			System.out.println("======================================================");
//
//			for(int i=1;i<=3;i++){
//				try {	
//					fc2o_m.execute(null);
//					HashMap<String,Object> hm = new HashMap<String,Object>();
//					hm.put("initWeight", ""+i);
//					aw_m.execute(hm);
//					
//					
//					String contribQuery = "" +
//					  "Select distinct c,i,w  \n" +
//					  "from \n" +
//					  "{c} <"+RelConstants.RL_OBSERVATION_ID+"> {i}, \n" +
//					  "{c} <"+RelConstants.RL_OBSERVATION_HAS_WEIGHT+"> {w} \n" +
//					  "";
//					
//					TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//					TupleQueryResult res = query.evaluate();
//					
//					while(res.hasNext()){
//						BindingSet bs = res.next();
//						System.out.println("Observation("+bs.getValue("i")+") has weight "+bs.getValue("w"));
//					}
//					
//				} catch (Exception e) {
//				}
//			}
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
