package pl.edu.icm.yadda.analysis.relations.auxil.parallel.bwmeta2sesame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.nativerdf.NativeStore;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Parallel;
import pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta.ExtensionFileIteratorBuilder;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;

/**
 * 
 * @author pdendek
 *
 */
public class ParallelActivator_FromBwmetaToSesame {

	static HashMap<String, String> hm = new HashMap<String, String>();
    static String BWMETA = "/home/pdendek/sample/ENHANCED/";
    static String SESAME = "/tmp/NATIVE_REPO_V2c";
    static String STEP = "100";
    
    private static void proceedArgs(String[] args) {
	  	for(int i=0; i+1<args.length;i+=2){
	  		String s = args[i];
	  		if(s.equals("BWMETA")) BWMETA = args[i+1];
	  		else if(s.equals("STEP")) STEP = args[i+1];
	  		else if(s.equals("SESAME")) SESAME = args[i+1];
	  	}
    }
    
    public static void main(String[] args) throws Throwable{
    	
    	proceedArgs(args);
    
    	String[] ext = {"bwmetaenhanced.xml"}; 
    	//create bwmeta from nlm iterator 
    	ExtensionFileIteratorBuilder o1 = new ExtensionFileIteratorBuilder();
    	hm.put(ExtensionFileIteratorBuilder.AUX_PARAM_SOURCE_DIR, BWMETA);  	
    	o1.setExtensions(ext);
    	ISourceIterator<File> itbwmeta = o1.build(hm);
    	
    	System.out.println("Przetworze teraz "+itbwmeta.getEstimatedSize()+" obiektów BWMETA'owych do repozytorium Sesamowego");
    	
    	File f = new File(SESAME);
    	if(!f.exists()){
    		f.mkdirs();
    	}
    	
    	NativeStore store=new NativeStore(f);
		SailRepository rep=new SailRepository(store);
		rep.initialize();
    	
    	ParallelOperator_FromBwmetaToSesame zeo = new ParallelOperator_FromBwmetaToSesame(BwmetaTransformers.BTF.getReader
				(BwmetaTransformers.BWMETA_1_2, BwmetaTransformers.Y),rep);
    	Parallel.CloneOperationFor(itbwmeta, zeo,Integer.parseInt(STEP));
//    	printRepository(rep);
    	
    	
    	HashMap<Integer,LinkedList<String>> rl = new HashMap<Integer,LinkedList<String>>(); 
    	
//    	for(Entry<String,AtomicInteger> e :  zeo.hm.entrySet()){
//    		if(rl.get(e.getValue().get())!=null) rl.get(e.getValue().get()).add(e.getKey());
//    		else{
//    			LinkedList<String> ll = new LinkedList<String>();
//    			ll.add(e.getKey());
//    			rl.put(e.getValue().get(), ll);
//    		}
////    		System.out.println(e.getKey()+": "+e.getValue().get());
//    	}
    	
    	for(Entry<Integer,LinkedList<String>> e :  rl.entrySet()){
    		StringBuilder sb = new StringBuilder();
    		sb.append("["+e.getKey()+": ");
    		Iterator<String> it = e.getValue().iterator();
    		int count=1;
    		sb.append(it.next());
    		while(it.hasNext()){
    			sb.append(", "+it.next());
    			count++;
    		}
    		sb.append("]");
    		System.out.println("["+count+"]"+sb.toString());
    	}
    	
    	System.out.println("==================================");
    	System.out.println("==================================");
//    	printRepository(rep);
    	System.out.println("==================================");
    	System.out.println("==================================");    	
    }
	
	public static void exportRepoToN3(Repository rep, String filePath) throws RepositoryException, FileNotFoundException, RDFHandlerException{
		File repoFile = new File(filePath);
		FileOutputStream fosRepo = new FileOutputStream(repoFile);
		RDFWriter repoWriter = Rio.createWriter(RDFFormat.N3, fosRepo);
		rep.getConnection().export(repoWriter);
	}
	
	public static SailRepository exportRepoToNativeStore(Repository rep, String dirPath) throws RepositoryException, FileNotFoundException, RDFHandlerException{
		File f = new File(dirPath);
		if(f.exists()) f.delete(); 
		f.mkdirs();
		NativeStore nativeStore = new NativeStore(f);
		SailRepository nativeRepo = new SailRepository(nativeStore);
		nativeRepo.initialize();
		
		RepositoryResult<Statement> rr = rep.getConnection().getStatements((Resource)null, null, null, false, (Resource)null);
		nativeRepo.getConnection().add(rr.asList(), (Resource)null);
		
		return nativeRepo;
	}
	
	public static void printRepository(SailRepository nr) throws RepositoryException{
		RepositoryResult<Statement> rr = nr.getConnection().getStatements((Resource)null, null, null, false, (Resource)null);
    	List<Statement> rl = rr.asList();
    	for(Statement s : rl){
    		System.out.println(
    				s.getSubject().toString()+"   ----(   "+
    				s.getPredicate().toString()+"   )--->   "+
    				s.getObject().toString());
    	}
	}
}

//public static void exportRepoToN3(Repository rep, String filePath) throws RepositoryException, FileNotFoundException, RDFHandlerException{
//	
//	
//	RepositoryResult<Statement> rr = rep.getConnection().getStatements((Resource)null, null, null, false, (Resource)null);
//	repoClone.getConnection().add(rr.asList(), (Resource)null);
//	
//	File repoFile = new File("/home/pdendek/sample/TEST_R/repofile.N3");
//	FileOutputStream fosRepo = new FileOutputStream(repoFile);
//	RDFWriter repoWriter = Rio.createWriter(RDFFormat.N3, fosRepo);
//	rep.getConnection().export(repoWriter);
//	rep.shutDown();
//	
//	File cloneFile = new File("/home/pdendek/sample/TEST_R/clonefile.N3");
//	FileOutputStream fosClone = new FileOutputStream(cloneFile);
//	RDFWriter cloneWriter = Rio.createWriter(RDFFormat.N3, fosClone);
//	repoClone.getConnection().export(cloneWriter);
//	repoClone.shutDown();
//}
