//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.io.File;
//import java.io.FileOutputStream;
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
//import org.openrdf.rio.RDFWriter;
//import org.openrdf.rio.Rio;
//import org.openrdf.sail.memory.MemoryStore;
//import org.openrdf.sail.nativerdf.NativeStore;
//
//import pl.edu.icm.yadda.analysis.relations.RepositoryManipulator;
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
//import pl.edu.icm.yadda.tools.reparser.Node;
//import pl.edu.icm.yadda.tools.reparser.RegexpParser;
//
//public class EmptyEmailRemover {
//
//	SesameManipulator c2o_m, rlttu_m, fc2o_m;
//	String filePath1 = "pl/edu/icm/yadda/analysis/relations/manipulations/operations/test1.N3";
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
//		rlttu_m = new SesameManipulator(repository, new LiteralTagAdressToUri());
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
//			rlttu_m.execute(null);
//			System.out.println("======================================================");
//			System.out.println("=====================REPAIRED=========================");
//			System.out.println("======================================================");
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
