package pl.edu.icm.yadda.analysis.relations.auxil.parallel.zbl2bwmeta;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Operation;
import pl.edu.icm.yadda.analysis.relations.auxil.trash.YToCatObjProcessingNode;
import pl.edu.icm.yadda.analysis.zentralblattimporter.nodes.ZBLtoYProcessingNode;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.transformers.Bwmeta2_0ToYTransformer;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.imports.zentralblatt.reading.ZentralBlattRecord;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectPart;

public class ParallelOperation_FromZblToBwmeta implements Operation<ZentralBlattRecord> {

    @SuppressWarnings("rawtypes")
	static IMetadataReader reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_2_0, BwmetaTransformers.Y);
    static Bwmeta2_0ToYTransformer transformer = new Bwmeta2_0ToYTransformer();
	
    public ParallelOperation_FromZblToBwmeta(String[] args){
    	proceedArgs(args);
    }
	
    ZBLtoYProcessingNode o2 = new ZBLtoYProcessingNode();
    YToCatObjProcessingNode o3 = new YToCatObjProcessingNode();
    
	static String ZBL_SRC_FILE;
    static String ZBL_DST_FOLDER;
    private static void proceedArgs(String[] args) {
	  	for(int i=0; i+1<args.length;i+=2){
	  		String s = args[i];
	  		if(s.equals("ZBL_DST_FOLDER")){
	  			ZBL_DST_FOLDER = args[i+1] + System.nanoTime();
	  			new File(ZBL_DST_FOLDER).mkdirs();
	  		}
	  		else if(s.equals("ZBL_SRC_FILE"))
	  			ZBL_SRC_FILE = args[i+1];
	  	}
    }
    
	@Override
	public void perform(ZentralBlattRecord zbr) {
		try{
    		YElement ye = null;
    		ye = o2.process(zbr, null);
    		List<YElement> yel = new LinkedList<YElement>(); 
    		yel.add(ye);
    		File f2 = new File(ZBL_DST_FOLDER,"Zbl"+zbr.getField(zbr.ID_FIELD_NAME)+".bwmeta.xml");
//    		System.out.println("Proceeding "+f2);
    		if(f2.exists()) return; 
    		FileWriter fw = new FileWriter(f2);
    		for(CatalogObject<String> co : o3.process(yel, null)){
    			for(CatalogObjectPart<String> cop : co.getParts()){
    				fw.write(cop.getData());
    				fw.flush();
        		}
        	}
    		fw.close();
    	}catch(Exception e){
    		System.out.println("Following exception occurred in record: "+zbr.toString());
    		e.printStackTrace();
    	}
	}

	@Override
	public Operation<ZentralBlattRecord> replicate() {
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
