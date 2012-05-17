package pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlmbwmetaPlusZblbwmetaPlusMixFile;


import java.io.File;
import java.util.HashMap;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Parallel;
import pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta.ExtensionFileIteratorBuilder;
import pl.edu.icm.yadda.analysis.relations.auxil.trash.YToCatObjProcessingNode;
import pl.edu.icm.yadda.bwmeta.transformers.Bwmeta2_0ToYTransformer;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;

/**
 * Slight modification of @author dtkaczyk AllFilesFromFolderIteratorBuilder
 *  @author pdendek
 */
public class ParallelActivator_GivenBwmetaWitZblBwmetaByMixFile_Enhancer {

    static IMetadataReader<?> reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_2_0, BwmetaTransformers.Y);
    static Bwmeta2_0ToYTransformer transformer = new Bwmeta2_0ToYTransformer();
    static HashMap<String, String> hm = new HashMap<String, String>();
    
    static ExtensionFileIteratorBuilder o1 = new ExtensionFileIteratorBuilder();
    static YToCatObjProcessingNode o3 = new YToCatObjProcessingNode();
    static String ENHANCE_NLM = "/home/pdendek/MIX_20111229/ENHANCED/";
    static String NLM = "/home/pdendek/MIX_20111229/NLM/"; 
    static String ZBL = "/tmp/DST_ZBL23472565045672/";
    static String MIX = "/home/pdendek/MIX.txt";
	static String STEP = "10";
    
    private static void proceedArgs(String[] args) {
	  	for(int i=0; i+1<args.length;i+=2){
	  		String s = args[i];
	  		if(s.equals("NLM"))
	  			NLM = args[i+1];
	  		else if(s.equals("ENHANCE_NLM"))
	  			ENHANCE_NLM = args[i+1];
	  		else if(s.equals("ZBL"))
	  			ZBL = args[i+1];
	  		else if(s.equals("MIX"))
	  			MIX = args[i+1];
	  		else if(s.equals("STEP"))
	  			STEP = args[i+1];
	  	}
    }
    
    @SuppressWarnings({ "static-access", "unused" })
	public static void main(String[] args) throws Throwable{
    	
    	proceedArgs(args);
    
    	if(!(new File(ENHANCE_NLM).exists()))
			new File(ENHANCE_NLM).mkdirs();
    	
    	String[] ext = {"5.bwmeta.xml"}; 
    	//create bwmeta from nlm iterator 
    	hm.put(o1.AUX_PARAM_SOURCE_DIR, NLM);  	
    	o1.setExtensions(ext);
    	ISourceIterator<File> itnlm = o1.build(hm);
    	
    	long start = System.nanoTime();
    	String fileStr = null;
    	
    	System.out.println("Przetworze teraz "+itnlm.getEstimatedSize()+" obiektów CEDRAMowych w formacie BWMETA");
    	
    	Parallel parallel = new Parallel();
    	
    	ParallelOperation_GivenBwmetaWitZblBwmetaByMixFile_Enhancer zeo = new ParallelOperation_GivenBwmetaWitZblBwmetaByMixFile_Enhancer(args);
    	parallel.For(itnlm, zeo, Integer.parseInt(STEP));
    }
}
