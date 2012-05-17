//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.io.File;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
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
//
//import pl.edu.icm.yadda.analysis.relations.RepositoryManipulator;
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
//import pl.edu.icm.yadda.tools.reparser.Node;
//import pl.edu.icm.yadda.tools.reparser.RegexpParser;
//
//public class FingerprintContributions2ObservationsTest {
//
//	SesameManipulator c2o_m;
//	SesameManipulator fc2o_m;
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
//		c2o_m = new SesameManipulator(repository, new Contributions2Observations());
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
//	
//	@Test
//	public void voidTest() {
//	}
//	
////	@Test
//	public void fc2o() {
//		try {
//			System.out.println("======================================================");
//			System.out.println("=====================FC2O=============================");
//			System.out.println("======================================================");
//
//			fc2o_m.execute(null);
//			
//			String contribQuery = "" +
//			  "Select distinct c,c1,c2,s1,s2,i  \n" +
//			  "from \n" +
//			  "{c} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c1}, \n" +
//			  "{c1} <"+RelConstants.RL_SURNAME+"> {s1}, \n" +
//			  "{c} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c2}, \n" +
//			  "{c2} <"+RelConstants.RL_SURNAME+"> {s2}, \n" +
//			  "{c} <"+RelConstants.RL_OBSERVATION_ID+"> {i} \n" +
//			  " WHERE \n " +
//			  "c1!=c2 \n" +
//			  "";
//			
//			TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			
//			boolean odd = true;
//			while(res.hasNext()){
//				BindingSet bs = res.next();
//				if(odd){
//					odd=false;
//					continue;
//				}else odd=true;
//				
//				System.out.println(bs.getValue("c1")+"\n"+bs.getValue("c2"));
//				System.out.println(bs.getValue("c")+":::["+bs.getValue("s1")+", "+bs.getValue("s2")+"]:::"+bs.getValue("i"));
//				System.out.println("-----------------------------------------------");
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
