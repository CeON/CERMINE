package pl.edu.icm.yadda.analysis.relations.auxil.parallel.zbl2bwmeta;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta.ExtensionFileIteratorBuilder;
import pl.edu.icm.yadda.analysis.relations.auxil.trash.YToCatObjProcessingNode;
import pl.edu.icm.yadda.analysis.zentralblattimporter.nodes.ZBLtoYProcessingNode;
import pl.edu.icm.yadda.bwmeta.transformers.Bwmeta2_0ToYTransformer;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.imports.zentralblatt.reading.ZentralBlattRecord;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;

/**
 * Slight modification of @author dtkaczyk AllFilesFromFolderIteratorBuilder
 *  @author pdendek
 */
public class ZblPersonFewSurnameCounter {

    static IMetadataReader<?> reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_2_0, BwmetaTransformers.Y);
    static Bwmeta2_0ToYTransformer transformer = new Bwmeta2_0ToYTransformer();
    static HashMap<String, String> hm = new HashMap<String, String>();
    
    static ExtensionFileIteratorBuilder o1 = new ExtensionFileIteratorBuilder();
    static YToCatObjProcessingNode o3 = new YToCatObjProcessingNode();
	static String ZBL_SRC_FILE;
    static String ZBL_DST_FOLDER;
//  static String NLM = "/home/pdendek/sample/NUMDAM/";
  
    private static void proceedArgs(String[] args) {
	  	for(int i=0; i+1<args.length;i+=2){
	  		String s = args[i];
	  		if(s.equals("ZBL_DST_FOLDER"))
	  			ZBL_DST_FOLDER = args[i+1];
	  		else if(s.equals("ZBL_SRC_FILE"))
	  			ZBL_SRC_FILE = args[i+1];
	  	}
    }
    	
    @SuppressWarnings({ "static-access", "unused" })
	public static void main(String[] args) throws Throwable{
    	
    	proceedArgs(args);
    
    	FromZblToBwmeta_IteratorBuilder o1 = new FromZblToBwmeta_IteratorBuilder();
    	o1.setInputStream(ZBL_SRC_FILE);
    	
    	ZBLtoYProcessingNode o2 = new ZBLtoYProcessingNode();
    	YToCatObjProcessingNode o3 = new YToCatObjProcessingNode(); 
    	
    	ISourceIterator<ZentralBlattRecord> it = o1.build(null);
    	
    	
    	HashMap<String, Set<String>> person2surname = new HashMap<String, Set<String>>(); 
    	HashMap<String, Set<String>> hm = person2surname;
    	
    	while(it.hasNext()){
    		ZentralBlattRecord src = it.next();
    		String[] ai = (src.hasField("ai")) ? src.getField("ai").split(";") : new String[0];
    	    String[] au = (src.hasField("au")) ? src.getField("au").split(";") : new String[0];
    	    
    	    for(int j=0;j<ai.length;j++)
    	    if(hm.containsKey(ai[j])){
    	    	if(hm.get(ai[j]).contains(au[j]));
    	    	else{
    	    		hm.get(ai[j]).add(au[j]);
    	    		System.out.println("Burf! Dla personality "+ai[j]+" wystapila kolizja nazwisk " +hm.get(ai[j])+" ORAZ "+au[j]);
    	    	}
    	    }else{
    	    	HashSet s = new HashSet();
    	    	s.add(au[j]);
    	    	hm.put(ai[j], s);
    	    }
    	    
    	    
    	    
    	}
    	
    	
    }
}
