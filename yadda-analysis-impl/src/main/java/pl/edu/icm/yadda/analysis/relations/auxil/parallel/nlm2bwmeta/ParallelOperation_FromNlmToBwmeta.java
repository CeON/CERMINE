package pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Operation;
import pl.edu.icm.yadda.analysis.relations.auxil.trash.YToCatObjProcessingNode;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.imports.transformers.NlmTagsFilter;
import pl.edu.icm.yadda.imports.transformers.NlmToYTransformer;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectPart;

public class ParallelOperation_FromNlmToBwmeta implements Operation<File> {

    @SuppressWarnings("rawtypes")
    static YToCatObjProcessingNode o3 = new YToCatObjProcessingNode();
    static NlmToYTransformer nlmToYransformer = new NlmToYTransformer();

    public ParallelOperation_FromNlmToBwmeta(){
    }
    
	@Override
	public void perform(File f) {
		System.out.println("\r"+f.getAbsolutePath());
		char[] buf;
		FileReader fr;
		String fileStr;
		try{
			buf = new char[(int) f.length()];
    		fr = new FileReader(f);
    		fr.read(buf);
    	    fileStr = new String(buf);

    	    fileStr = NlmTagsFilter.filterTagsInNlmBody(fileStr);
    	    fileStr = NlmTagsFilter.filterUnsupportedTags(fileStr);
    		List<YExportable> yexl = nlmToYransformer.read(fileStr, (Object[]) null); 
    		List<YElement> yel = new LinkedList<YElement>(); 
    		
    		List<YExportable> lst = yexl;
    		for(YExportable yex : yexl)
    			if(yex instanceof YElement) yel.add((YElement)yex);
    		
        	File fd = new File(f.getAbsolutePath().substring(0,f.getAbsolutePath().length()-4)+".bwmeta.xml");
    		if(fd.exists()){
    			fd.delete();
    			fd.createNewFile();
    		}
        	
    		int j=0;
    		for(CatalogObject<String> co : o3.process(yel, null)){
    			for(CatalogObjectPart<String> cop : co.getParts()){
    	    		File f2 = new File(f.getAbsolutePath().substring(0,f.getAbsolutePath().length()-4)+"."+j+".bwmeta.xml");
    	    		if(f2.exists()){
    	    			f2.delete();
    	    			f2.createNewFile();
    	    		}
    	    		
    	    		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(f2),"UTF-8");
    	    		out.write(cop.getData());
    				out.flush();
    				out.close();
        		}
    			j++;
        	}        		
        }catch(Exception e){
        	System.out.println("In file: "+f.getAbsolutePath());
        	System.out.println("Following exception occured:");
        	e.printStackTrace();
        }
	}




	@Override
	public Operation<File> replicate() {
		return this;
	}

	@Override
	public void setUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutDown() {
		// TODO Auto-generated method stub
		
	}	
}
