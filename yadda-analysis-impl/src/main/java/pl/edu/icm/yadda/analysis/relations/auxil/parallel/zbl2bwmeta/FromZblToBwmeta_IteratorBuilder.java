package pl.edu.icm.yadda.analysis.relations.auxil.parallel.zbl2bwmeta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.auxil.trash.YToCatObjProcessingNode;
import pl.edu.icm.yadda.analysis.zentralblattimporter.nodes.ZBLtoYProcessingNode;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.imports.zentralblatt.reading.ZentralBlattRecord;
import pl.edu.icm.yadda.imports.zentralblatt.reading.ZentralBlattTextIterator;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.iterator.IIdExtractor;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.process.iterator.ISourceIteratorBuilder;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectPart;

/**
 * Iterates over stream of ZentralBlattMATH text file with records.
 * 
 * Adapts ZentralBlattTextIterator class for use of yadda processing services.
 * 
 * @author tkusm
 *
 */
public class FromZblToBwmeta_IteratorBuilder implements
		ISourceIteratorBuilder<ZentralBlattRecord> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Stream with input data (ZBL records).
	 */
	private InputStream inputStream = null;
	/**
	 * Says what is parameter name that contains path to the input file.
	 * 
	 * AUX parameter name from ProcessContext.
	 */
	private String inFilePathParameterName = null;

	private File file;

	public FromZblToBwmeta_IteratorBuilder() {
		
	}
	
	public FromZblToBwmeta_IteratorBuilder(InputStream is) {
		this.inputStream = is;
	}
	
	public FromZblToBwmeta_IteratorBuilder(String inFilePathParameterName) {
		this.inFilePathParameterName = inFilePathParameterName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(String filePath) throws FileNotFoundException {
		this.inputStream = new FileInputStream(filePath);
		this.file = new File(filePath);
		
	}
	

	public void setInFilePathParameterName(String inFilePathParameterName) {
		this.inFilePathParameterName = inFilePathParameterName;
	}



	static class IZentralBlattRecordSourceIterator 
			extends ZentralBlattTextIterator 
			implements ISourceIterator<ZentralBlattRecord> {

		private File file;
		private Integer estimatedSize = null;

		public IZentralBlattRecordSourceIterator(File file) throws FileNotFoundException {
			super(
					new FileInputStream(file)
				);
			this.file=file;
		}

		@Override
		synchronized public int getEstimatedSize() throws UnsupportedOperationException {
			if(estimatedSize==null){
				try{
					BufferedReader br = new BufferedReader(new FileReader(this.file));
					int all = 0;
					for(String s=br.readLine();s!=null;s=br.readLine()){
						Matcher matcher1 = Pattern.compile("^an",Pattern.UNICODE_CASE|Pattern.CANON_EQ|Pattern.CASE_INSENSITIVE|Pattern.DOTALL).matcher(new StringBuilder(s));
		        		if(!matcher1.find()) all++;
					}
					this.estimatedSize = all;
				}catch(Exception e){
					this.estimatedSize  = 0;
				}
			}
			return estimatedSize;
		}

		@Override
		public void clean() {
			/*try {
				this.input.close();
			} catch (IOException e) {
			} */
		}

	}


	@Override
	public ISourceIterator<ZentralBlattRecord> build(ProcessContext ctx)
			throws Exception {
		checkIfStreamInitialized(ctx);

		log.info("[build] inputStream={}",inputStream);
		return new IZentralBlattRecordSourceIterator(this.file);
	}
	
	private void checkIfStreamInitialized(ProcessContext ctx)
			throws FileNotFoundException {
		if (inputStream == null && inFilePathParameterName!=null) {
			String inputFilePath = (String) ctx.getAuxParam(inFilePathParameterName);
			inputStream = new FileInputStream(inputFilePath);
		}
	}

	@Override
	public IIdExtractor<ZentralBlattRecord> getIdExtractor() {
		return new IIdExtractor<ZentralBlattRecord>() {

			@Override
			public String getId(ZentralBlattRecord element) {
				return element.getField(ZentralBlattRecord.ID_FIELD_NAME);
			}			
		};
	}
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
	
	 @SuppressWarnings({ "static-access", "rawtypes", "unused", "unchecked" })
		public static void main(String[] args) throws Throwable{
		 
		proceedArgs(args);
		 
		File f3 = new File("/tmp/log.info");
 		FileWriter fwerr = new FileWriter(f3);

 		String fPath =  ZBL_DST_FOLDER;
		 
    	FromZblToBwmeta_IteratorBuilder o1 = new FromZblToBwmeta_IteratorBuilder();
    	o1.setInputStream(ZBL_SRC_FILE);
    	
    	ZBLtoYProcessingNode o2 = new ZBLtoYProcessingNode();
    	YToCatObjProcessingNode o3 = new YToCatObjProcessingNode(); 
    	
    	ISourceIterator<ZentralBlattRecord> it = o1.build(null);
    	int i = 0;
    	while(it.hasNext()){
    		i++;
    		ZentralBlattRecord zbr = it.next();
    		File f2 = new File(ZBL_DST_FOLDER+"Zbl"+zbr.getField(zbr.ID_FIELD_NAME)+".bwmeta.xml");
//    		System.out.println("Proceeding "+f2);
    		if(f2.exists()) continue; 
    		YElement ye = null;
    		try{
    			ye = o2.process(zbr, null);
    		}catch(Exception e){
    			fwerr.write("Error! in the given below record:\n");
    			fwerr.write(zbr.toString()+"\n");
    			fwerr.write("following error occurred:\n");
    			fwerr.write(e.getStackTrace()+"\n");
    			fwerr.flush();
    			e.printStackTrace();
    			continue;
    		}
    		List<YElement> yel = new LinkedList<YElement>(); 
    		yel.add(ye);
    		FileWriter fw = new FileWriter(f2);
    		for(CatalogObject<String> co : o3.process(yel, null)){
    			for(CatalogObjectPart<String> cop : co.getParts()){
    				fw.write(cop.getData());
    				fw.flush();
        		}
        	}
    		fw.close();
    	}
    	fwerr.close();
	 }
	
}
