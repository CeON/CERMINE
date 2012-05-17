package pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta;



import java.io.File;
import java.util.HashMap;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Parallel;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;

/**
 * Slight modification of @author dtkaczyk AllFilesFromFolderIteratorBuilder
 *  @author pdendek
 */
public class ParallelActivator_FromNlmToBwmeta {

    static HashMap<String, String> hm = new HashMap<String, String>();
    static String NLM = "/home/pdendek/sample/CEDRAM/";
    
    private static void proceedArgs(String[] args) {
	  	for(int i=0; i+1<args.length;i+=2){
	  		String s = args[i];
	  		if(s.equals("NLM")) NLM = args[i+1];
	  	}
    }
    
	public static void main(String[] args) throws Throwable{
    	
    	proceedArgs(args);
    
    	String[] ext = {"xml"}; 
    	//create bwmeta from nlm iterator 
    	ExtensionFileIteratorBuilder o1 = new ExtensionFileIteratorBuilder();
    	hm.put(ExtensionFileIteratorBuilder.AUX_PARAM_SOURCE_DIR, NLM);  	
    	o1.setExtensions(ext);
    	ISourceIterator<File> itnlm = o1.build(hm);
    	
    	long start = System.nanoTime();
    	String fileStr = null;
    	
    	System.out.println("Przetworze teraz "+itnlm.getEstimatedSize()+" obiektów CEDRAMowych w formacie BWMETA");
    	
    	Parallel parallel = new Parallel();
    	
    	ParallelOperation_FromNlmToBwmeta zeo = new ParallelOperation_FromNlmToBwmeta();
    	parallel.For(itnlm, zeo);
    }
}
