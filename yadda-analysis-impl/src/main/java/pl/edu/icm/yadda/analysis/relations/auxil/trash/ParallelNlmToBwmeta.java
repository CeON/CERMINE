package pl.edu.icm.yadda.analysis.relations.auxil.trash;


import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Parallel;
import pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta.ExtensionFileIteratorBuilder;
import pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlmbwmetaPlusZblbwmetaPlusMixFile.ParallelOperation_GivenBwmetaWitZblBwmetaByMixFile_Enhancer;
import pl.edu.icm.yadda.bwmeta.transformers.Bwmeta2_0ToYTransformer;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;

/**
 * Slight modification of @author dtkaczyk AllFilesFromFolderIteratorBuilder
 *  @author pdendek
 */
public class ParallelNlmToBwmeta{
	static IMetadataReader<?> reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_2_0, BwmetaTransformers.Y);
    static Bwmeta2_0ToYTransformer transformer = new Bwmeta2_0ToYTransformer();
    static HashMap<String, String> hm = new HashMap<String, String>();
    
    static ExtensionFileIteratorBuilder o1 = new ExtensionFileIteratorBuilder();
    static YToCatObjProcessingNode o3 = new YToCatObjProcessingNode();
    static String ENHANCE_NLM = "/home/pdendek/sample/ENHANCE_3/";
    static String NLM = "/home/pdendek/sample/CEDRAM/";
//    static String NLM = "/home/pdendek/sample/NUMDAM/";
    static String ZBL = "/home/pdendek/sample/ZBL/";
    static String MIX = "/home/pdendek/MIX.txt";
    
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

    	LinkedList<File> flist = new LinkedList<File>();
    	for(int iteration = 0;itnlm.hasNext() && iteration<100;iteration++) flist.add(itnlm.next());
    	
    	Parallel parallel = new Parallel();
    	
    	ParallelOperation_GivenBwmetaWitZblBwmetaByMixFile_Enhancer zeo = new ParallelOperation_GivenBwmetaWitZblBwmetaByMixFile_Enhancer(args);
    	parallel.For(flist, zeo);
    }

    
    private static void proceedArgs(String[] args) {
    	for(String s : args){
    		if(s.split("=")[0].equals("NLM"))
    			NLM = s.split("=")[1];
    		if(s.split("=")[0].equals("ENHANCE_NLM"))
    			ENHANCE_NLM = s.split("=")[1];
    		if(s.split("=")[0].equals("ZBL"))
    			ZBL = s.split("=")[1];
    		if(s.split("=")[0].equals("MIX"))
    			MIX = s.split("=")[1];
    	}
	}

}
