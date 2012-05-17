//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.io.File;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//
//import org.apache.tools.ant.types.CommandlineJava.SysProperties;
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
//import org.openrdf.repository.RepositoryException;
//import org.openrdf.repository.RepositoryResult;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.rio.RDFFormat;
//import org.openrdf.sail.memory.MemoryStore;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
//import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;
//import pl.edu.icm.yadda.analysis.relations.pj.clues.PJNotStrictLanguageClue;
//import pl.edu.icm.yadda.analysis.relations.pj.proofs.PJEmailProof;
//import pl.edu.icm.yadda.tools.relations.Statements;
//
//public class OneRunWeightAssignationTest {
//
//	SesameManipulator fc2o_m, aw_m, atsi_m, wr_m;
//	String filePath1 = "pl/edu/icm/yadda/analysis/relations/manipulations/operations/test2.N3";
//	LinkedList<PJDisambiguator> featureList = new LinkedList<PJDisambiguator>(); 
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
//		wr_m = new SesameManipulator(repository, new WeightRecalculatorAdv());
//		
//		PJDisambiguator clue = new PJEmailProof();
//		clue.setRepository(rep);
//		featureList.add(clue);
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
//			  "Select distinct s1,s2,c,i,same,c1,c2,p1,p2  \n" +
//			  "from \n" +
//			  "{c} <"+RelConstants.RL_HAS_OBSERVATION_ID+"> {i}, \n" +
//			  "{c} <"+RelConstants.RL_HAS_OBSERVATION_CONTRIBUTOR+"> {c1}, \n" +
//			  "{c} <"+RelConstants.RL_HAS_OBSERVATION_CONTRIBUTOR+"> {c2}, \n" +
//			  "{c1} <"+RelConstants.RL_IS_PERSON+"> {p1}, \n" +
//			  "{c2} <"+RelConstants.RL_IS_PERSON+"> {p2}, \n" +
//			  "{c1} <"+RelConstants.RL_SURNAME+"> {s1}, \n" +
//			  "{c2} <"+RelConstants.RL_SURNAME+"> {s2}, \n" +
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
//				ValueFactory vf = repository.getValueFactory();
//
//				int featureIndex = 0;
//				for(PJDisambiguator clue : featureList){
//					repository.getConnection().add(vf.createURI(bs.getValue("c").stringValue()), 
//							vf.createURI(RelConstants.RL_OBSERVATION_CONTAINS_SAME_PERSON+"#f"+featureIndex),
//							clue.analyze(bs.getValue("c1").stringValue(), 
//									bs.getValue("c2").stringValue()) > 0 ? 
//											vf.createLiteral("TRUE")  :
//											vf.createLiteral("FALSE"),  
//							(Resource)null);
//				}
//			}
//			
//			LinkedList<String> ls = new LinkedList<String>();
//			ls.add(RelConstants.RL_OBSERVATION_CONTAINS_SAME_PERSON);
//			ls.add(RelConstants.RL_OBSERVATION_CONTAINS_SAME_PERSON+"#f1");
//			ls.add(RelConstants.RL_OBSERVATION_HAS_WEIGHT);
//			printRepositoryPredicate(repository, ls);
//			
//			hm = new HashMap<String,Object>();
//			hm.put("testedIsPersonRelation", RelConstants.RL_OBSERVATION_CONTAINS_SAME_PERSON+"#f1");
//			wr_m.execute(hm);
//			
//			System.out.println("==================================");
//			System.out.println("============AFTER=================");
//			System.out.println("==================================");
//			printRepositoryPredicate(repository, ls);
//			
//		} catch (Exception e) {
//		}
//	}
//	
//	
//	private void printRepositoryPredicate(Repository nr,
//			LinkedList<String> ls) throws RepositoryException {
//		
//		for(String s : ls){
//			RepositoryResult<Statement> rr = nr.getConnection().getStatements((Resource)null, nr.getValueFactory().createURI(s), null, false, (Resource)null);
//	    	List<Statement> rl = rr.asList();
//	    	for(Statement s1 : rl){
//	    		System.out.println(
//	    				s1.getSubject().toString()+"   ----(   "+
//	    				s1.getPredicate().toString()+"   )--->   "+
//	    				s1.getObject().toString());
//	    	}
//		}
//		
//		
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
