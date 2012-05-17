package pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2mallettrain;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Parallel;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;

/**
 * Slight modification of @author dtkaczyk AllFilesFromFolderIteratorBuilder
 *  @author pdendek
 */
public class ParallelActivator_FromNlmToMalletInputData {

    static HashMap<String, String> hm = new HashMap<String, String>();
//    static String[] NLM = {"/home/pdendek/dane_icm/parsowanie_cytowan_crfem/metadata-extraction/refs-parsing/refs-parsing-train",
//    						"/home/pdendek/dane_icm/parsowanie_cytowan_crfem/metadata-extraction/refs-parsing/refs-parsing-test"};
//    static String[] NLM = {"/home/pdendek/dane_icm/parsowanie_cytowan_crfem/metadata-extraction/refs-parsing"};
    static String[] NLM = {"/home/pdendek/dane_icm/parsowanie_cytowan_crfem/NUMDAM"};
    
    private static void proceedArgs(String[] args) {
    	ArrayList<String> nlm = new ArrayList<String>();
	  	for(int i=0; i+1<args.length;i+=2){
	  		String s = args[i];
	  		if(s.equals("NLM")){
	  			nlm.add(args[i+1]);
	  		}
	  	}
	  	if(!nlm.isEmpty()) NLM = (String[]) nlm.toArray();
    }
    
	public static void main(String[] args) throws Throwable{
    	proceedArgs(args);
    	for(String nlm : NLM){
    		inner_main(nlm);
    	}
	}

	private static void inner_main(String nlm) throws Exception {
		String[] ext = {"xml"}; 
    	//create bwmeta from nlm iterator 
    	FromNlmToBwmeta_IteratorBuilder o1 = new FromNlmToBwmeta_IteratorBuilder();
    	hm.put(FromNlmToBwmeta_IteratorBuilder.AUX_PARAM_SOURCE_DIR, nlm);  	
    	o1.setExtensions(ext);
    	ISourceIterator<File> itnlm = o1.build(hm);
    	
    	long start = System.nanoTime();
    	String fileStr = null;
    	
    	System.out.println("Przetworze teraz "+itnlm.getEstimatedSize()+" obiektów CEDRAMowych w formacie BWMETA");
    	
    	Parallel parallel = new Parallel();
    	
    	ParallelOperation_FromNlmToMalletInputData zeo = new ParallelOperation_FromNlmToMalletInputData();
    	parallel.For(itnlm, zeo);
	}
}
