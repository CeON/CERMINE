package pl.edu.icm.yadda.analysis.relations.auxil.parallel.bwmeta2surnameSesame;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Operation;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.transformers.Bwmeta2_0ToYTransformer;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.tools.relations.SesameFeeder;
import pl.edu.icm.yadda.tools.relations.Statements;


/**
 * 
 * @author pdendek
 *
 */
public class ParallelOperator_FromBwmetaToSurnameSesame implements Operation<File>{
	
	public final static HashMap<String,AtomicInteger> hm = new HashMap<String,AtomicInteger>(); 
	final SesameFeeder sf = new SesameFeeder();
	final ExtendedSurnameRelationshipStatementsBuilder esb = new ExtendedSurnameRelationshipStatementsBuilder(hm);
	final protected IMetadataReader<YExportable> reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_2_0, BwmetaTransformers.Y);
	final protected Bwmeta2_0ToYTransformer transformer = new Bwmeta2_0ToYTransformer();
	static Random rnd = new Random(System.nanoTime());
	String repositoryPathCore = null; 
	static Object obj = new Object();
	
	
	public ParallelOperator_FromBwmetaToSurnameSesame(){
		esb.setDefaultParser();
    }
	
	@Override
	public void perform(File f) {
		try{			
			LinkedList<YExportable> lye = new LinkedList<YExportable>();
			for(YElement e : toYElements(f)) lye.add(e);
			
			ReturnObject ro = esb.buildStatements(lye);
			List<Statements> statements = ro.getStatements();
			List<String> surnames = ro.getSurnames();
			for(String s : surnames){
		    	Repository r = getOrCreate(repositoryPathCore+s);
		    	sf.setRepository(r);
				sf.store(statements);
			}
        }catch(Exception e){
    		try{
    			System.out.println("Following exception occurred in file: "+f.getAbsolutePath());
    		}catch(Exception NullPointerException){
    			System.out.println("Following exception occurred in file: >>Null<<");
    		}
    		e.printStackTrace();
    	}
	}

	private List<YElement> toYElements(File f) throws Exception {
		FileReader fr = new FileReader(f);
		List<YExportable> lye = reader.read(fr, null);
		fr.close();
		fr=null;
		List<YElement> yel = new LinkedList<YElement>();
		for(YExportable ye : lye)
			if(ye instanceof YElement) yel.add((YElement)ye);
		return yel;
	}

	@Override
	public Operation<File> replicate() {
		ParallelOperator_FromBwmetaToSurnameSesame zeo = new ParallelOperator_FromBwmetaToSurnameSesame();
    	zeo.setRepositoryPathCore(this.repositoryPathCore);
    	return zeo;
	}

	public void setRepositoryPathCore(String repositoryPathCore) {
		this.repositoryPathCore=repositoryPathCore;
	}
	
	public void setUp(){
	}
	
	public void shutDown(){
	}
	
	protected Repository getOrCreate(String id) throws RepositoryException {
		Repository newRep = createRepo(id);
		return newRep;
	}
	
	public void initialize(Repository r){
		try {
			r.initialize();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Repository createRepo(String repoPath) throws RepositoryException{
		File fin = new File(repoPath);
		if(!fin.exists()){
			synchronized(this.obj){
				if(!fin.exists()){
					fin.mkdirs();
					NativeStore store=new NativeStore(fin);
					SailRepository rep=new SailRepository(store);
					try{
						rep.initialize();
					}catch(RepositoryException e){
						System.out.println(e);
						//we can proceed with no problem with this exception 
					}
					return rep;
				}else{
					NativeStore store=new NativeStore(fin);
					SailRepository rep=new SailRepository(store);
				}
			}
		}
		NativeStore store=new NativeStore(fin);
		SailRepository rep=new SailRepository(store);
		try{
			rep.initialize();
		}catch(RepositoryException e){
			System.out.println(e);
			//we can proceed with no problem with this exception 
		}
		return rep;
	}
}
