//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.io.File;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.Map;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.openrdf.model.Resource;
//import org.openrdf.model.URI;
//import org.openrdf.model.ValueFactory;
//import org.openrdf.query.BindingSet;
//import org.openrdf.query.QueryLanguage;
//import org.openrdf.query.TupleQuery;
//import org.openrdf.query.TupleQueryResult;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryConnection;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.rio.RDFFormat;
//import org.openrdf.sail.memory.MemoryStore;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
//
//public class AreTheSameInitializeTest{
//
//	SesameManipulator fc2o_m;
//	SesameManipulator aw_m;
//	SesameManipulator atsi_m;
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
//		atsi_m =  new SesameManipulator(repository, new AreTheSameBooleanInitializer());
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
//	public void wzor() {
//		try {
//			System.out.println("======================================================");
//			System.out.println("=====================WZOR==============================");
//			System.out.println("======================================================");
//
//			fc2o_m.execute(null);
//			HashMap<String,Object> hm = new HashMap<String,Object>();
//			hm.put("initWeight", "1");
//			aw_m.execute(hm);
//			
//			hm = new HashMap<String,Object>();
//			hm.put("isPersonPredicate", RelConstants.RL_IS_PERSON);
//			atsi_m.execute(hm);
//			
//			String contribQuery = "" +
//			  "Select distinct c,i,same,c1,c2,p1,p2  \n" +
//			  "from \n" +
//			  "{c} <"+RelConstants.RL_HAS_OBSERVATION_ID+"> {i}, \n" +
//			  "{c} <"+RelConstants.RL_HAS_OBSERVATION_CONTRIBUTOR+"> {c1}, \n" +
//			  "{c} <"+RelConstants.RL_HAS_OBSERVATION_CONTRIBUTOR+"> {c2}, \n" +
//			  "{c1} <"+RelConstants.RL_IS_PERSON+"> {p1}, \n" +
//			  "{c2} <"+RelConstants.RL_IS_PERSON+"> {p2}, \n" +
//			  "{c} <"+RelConstants.RL_OBSERVATION_CONTAINS_SAME_PERSON+"> {same} \n " +
//			  		" WHERE \n" +
//			  		" c1!=c2 \n" +
//			  "";
//			
//			TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			
//			LinkedList<String> ll = new LinkedList<String>(); 
//			while(res.hasNext()){
//				BindingSet bs = res.next();
//				if(ll.contains(bs.getValue("i").toString())) continue;
//				else ll.add(bs.getValue("i").toString());
//				
//				System.out.println("Observation("+bs.getValue("i")+") conteins contribs:\n"
//						+bs.getValue("c1")+"\n"
//						+bs.getValue("c2")+"\n"
//						+" with personId:\n" 
//						+bs.getValue("p1")+"\n"
//						+bs.getValue("p2")+"\n"
//						+" is treated as SAME person by database: "+bs.getValue("same"));  
//				System.out.println("\n\n\n");
//			}
//			
//		} catch (Exception e) {
//		}
//	}
//}