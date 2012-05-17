package pl.edu.icm.yadda.analysis.relations.general2sesame.auxil;
//package pl.edu.icm.yadda.analysis.relations.general2sesame.bwmeta2bigdatasesame;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Properties;
//
//import org.openrdf.model.Resource;
//import org.openrdf.model.Statement;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryException;
//import org.openrdf.repository.RepositoryResult;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.rio.RDFFormat;
//import org.openrdf.rio.RDFHandlerException;
//import org.openrdf.rio.RDFWriter;
//import org.openrdf.rio.Rio;
//import org.openrdf.sail.nativerdf.NativeStore;
//
//import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Parallel;
//import pl.edu.icm.yadda.analysis.relations.auxil.parallel.bwmeta2sesame.ParallelOperator_FromBwmetaToSesame;
//import pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta.ExtensionFileIteratorBuilder;
//import pl.edu.icm.yadda.process.iterator.ISourceIterator;
//
//import com.bigdata.btree.IndexMetadata;
//import com.bigdata.rdf.sail.BigdataSail;
//import com.bigdata.rdf.sail.BigdataSailRepository;
//import com.bigdata.samples.SampleCode;
//
///**
// * 
// * @author pdendek
// *
// */
//public class ParallelActivator_FromBwmetaToBigDataSesame {
//
//	static HashMap<String, String> hm = new HashMap<String, String>();
//    static String BWMETA = "/home/pdendek2/sample/ENHANCED/";
//    static String SESAME = "sesame_and";
//    static String STEP = "100";
//    static String BIGDATA = "FALSE";
//    
//    
//    private static void proceedArgs(String[] args) {
//	  	for(int i=0; i+1<args.length;i+=2){
//	  		String s = args[i];
//	  		if(s.equals("BWMETA")) BWMETA = args[i+1];
//	  		else if(s.equals("SESAME")) SESAME = args[i+1];
//	  		else if(s.equals("STEP")) STEP = args[i+1];
//	  		else if(s.equals("BIGDATA")) BIGDATA = args[i+1];
//	  	}
//    }
//    
//    public static void main(String[] args) throws Throwable{
//    	
//    	proceedArgs(args);
//    
//    	String[] ext = {"bwmetaenhanced.xml"}; 
//    	//create bwmeta from nlm iterator 
//    	ExtensionFileIteratorBuilder o1 = new ExtensionFileIteratorBuilder();
//    	hm.put(ExtensionFileIteratorBuilder.AUX_PARAM_SOURCE_DIR, BWMETA);  	
//    	o1.setExtensions(ext);
//    	ISourceIterator<File> itbwmeta = o1.build(hm);
//    	System.out.println("Przetworze teraz "+itbwmeta.getEstimatedSize()+" obiektów BWMETA'owych do repozytorium Sesamowego");
//    	//create SAIL repository
//    	Repository repo = null;
//    	if(BIGDATA.equals("TRUE")) repo = createBigdataRepository();
//    	else repo = createSesameRepository("/tmp/"+SESAME+System.nanoTime());
//    	repo.initialize();
//    	//proceed nlm import
//    	
//    	
//    	ParallelOperator_FromBwmetaToSesame zeo = new ParallelOperator_FromBwmetaToSesame();
//    	zeo.setRepository(repo);
//    	Parallel.For(itbwmeta, zeo,Integer.parseInt(STEP));
//    }
//	
//	private static Repository createSesameRepository(String path) {
//		File f = new File(path);
//		f.mkdirs();
//		NativeStore store=new NativeStore(f);
//		return new SailRepository(store);
//	}
//
//	private static Repository createBigdataRepository() throws Exception {
//		final String propertiesFile = "web.properties";
//        final Properties properties = new SampleCode().loadProperties(propertiesFile);
//
//        properties.setProperty(
//                IndexMetadata.Options.WRITE_RETENTION_QUEUE_CAPACITY,
//                "8000");
//
//        // when loading a large data file, it's sometimes better to do
//        // database-at-once closure rather than incremental closure.  this
//        // is how you do it.
//        properties.setProperty(BigdataSail.Options.TRUTH_MAINTENANCE, "false");
//
//        // we won't be doing any retraction, so no justifications either
//        properties.setProperty(BigdataSail.Options.JUSTIFY, "false");
//
//        // no free text search
//        properties.setProperty(BigdataSail.Options.TEXT_INDEX, "false");
//
//        // no statement identifiers
//        properties.setProperty(BigdataSail.Options.STATEMENT_IDENTIFIERS,
//                "false");
//
//        // triples only.
//        properties.setProperty(
//                com.bigdata.rdf.store.AbstractTripleStore.Options.QUADS,
//                "false");
//        
//        if (properties.getProperty(com.bigdata.journal.Options.FILE) == null) {
//        	
//        	File journal = File.createTempFile(SESAME, ".jnl");
//            properties.setProperty(BigdataSail.Options.FILE, journal
//                    .getAbsolutePath());
//        }
//            
//        // instantiate a sail
//        BigdataSail sail = new BigdataSail(properties);
//        return new BigdataSailRepository(sail);
//	}
//
//	public static void exportRepoToN3(Repository rep, String filePath) throws RepositoryException, FileNotFoundException, RDFHandlerException{
//		File repoFile = new File(filePath);
//		FileOutputStream fosRepo = new FileOutputStream(repoFile);
//		RDFWriter repoWriter = Rio.createWriter(RDFFormat.N3, fosRepo);
//		rep.getConnection().export(repoWriter);
//	}
//	
//	public static SailRepository exportRepoToNativeStore(Repository rep, String dirPath) throws RepositoryException, FileNotFoundException, RDFHandlerException{
//		File f = new File(dirPath);
//		if(f.exists()) f.delete(); 
//		f.mkdirs();
//		NativeStore nativeStore = new NativeStore(f);
//		SailRepository nativeRepo = new SailRepository(nativeStore);
//		nativeRepo.initialize();
//		
//		RepositoryResult<Statement> rr = rep.getConnection().getStatements((Resource)null, null, null, false, (Resource)null);
//		nativeRepo.getConnection().add(rr.asList(), (Resource)null);
//		
//		return nativeRepo;
//	}
//	
//	public static void printRepository(SailRepository nr) throws RepositoryException{
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
//
////public static void exportRepoToN3(Repository rep, String filePath) throws RepositoryException, FileNotFoundException, RDFHandlerException{
////	
////	
////	RepositoryResult<Statement> rr = rep.getConnection().getStatements((Resource)null, null, null, false, (Resource)null);
////	repoClone.getConnection().add(rr.asList(), (Resource)null);
////	
////	File repoFile = new File("/home/pdendek/sample/TEST_R/repofile.N3");
////	FileOutputStream fosRepo = new FileOutputStream(repoFile);
////	RDFWriter repoWriter = Rio.createWriter(RDFFormat.N3, fosRepo);
////	rep.getConnection().export(repoWriter);
////	rep.shutDown();
////	
////	File cloneFile = new File("/home/pdendek/sample/TEST_R/clonefile.N3");
////	FileOutputStream fosClone = new FileOutputStream(cloneFile);
////	RDFWriter cloneWriter = Rio.createWriter(RDFFormat.N3, fosClone);
////	repoClone.getConnection().export(cloneWriter);
////	repoClone.shutDown();
////}
