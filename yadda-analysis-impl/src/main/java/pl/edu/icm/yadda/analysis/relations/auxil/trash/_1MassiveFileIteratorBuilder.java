package pl.edu.icm.yadda.analysis.relations.auxil.trash;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.transformers.Bwmeta2_0ToYTransformer;
import pl.edu.icm.yadda.imports.transformers.NlmToYTransformer;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.iterator.IIdExtractor;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.process.iterator.ISourceIteratorBuilder;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectPart;

/**
 * Slight modification of @author dtkaczyk AllFilesFromFolderIteratorBuilder
 *  @author pdendek
 */
public class _1MassiveFileIteratorBuilder implements ISourceIteratorBuilder<File> {

    public static final String AUX_PARAM_SOURCE_DIR = "source_dir";
    public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public String[] getExtensions() {
		return extensions;
	}

	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	public static String getAuxParamSourceDir() {
		return AUX_PARAM_SOURCE_DIR;
	}

	private String sourceDir;
    private String[] extensions;
    private Collection<File> files;
    
    @SuppressWarnings("unchecked")
	public ISourceIterator<File> build(Map<String,String> ctx) throws Exception {
        String dirPath = sourceDir;
        if (ctx.get(AUX_PARAM_SOURCE_DIR) != null) {
            dirPath = (String)ctx.get(AUX_PARAM_SOURCE_DIR);
        }

        File sourceFile = new File(dirPath);
        if (!sourceFile.isDirectory()) {
            throw new InvalidParameterException(sourceFile.getAbsolutePath() + " is not a directory!");
        }

        files = FileUtils.listFiles(sourceFile, extensions, false);
        return new NLMFieleIterator(files);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public ISourceIterator<File> build(ProcessContext ctx) throws Exception {
        String dirPath = sourceDir;
        if (ctx.containsAuxParam(AUX_PARAM_SOURCE_DIR)) {
            dirPath = (String)ctx.getAuxParam(AUX_PARAM_SOURCE_DIR);
        }

        File sourceFile = new File(dirPath);
        if (!sourceFile.isDirectory()) {
            throw new InvalidParameterException(sourceFile.getAbsolutePath() + " is not a directory!");
        }

        files = FileUtils.listFiles(sourceFile, extensions, false);
        return new NLMFieleIterator(files);
    }

    
    static class NLMFieleIterator implements ISourceIterator<File> {

    	private Collection<File> files;
		private Iterator<File> iterator;

		public NLMFieleIterator(Collection<File> files){
    		this.files = files;
    		iterator = files.iterator();
    	}
    	
    	
		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public File next() {
			return iterator.next();
		}

		@Override
		public void remove() {
			throw new NotImplementedException();
		}

		@Override
		public int getEstimatedSize() throws UnsupportedOperationException {
			return files.size();
		}

		@Override
		public void clean() {
			//FIXME be silent like a ninja
		}
    }

    @Override
    public IIdExtractor<File> getIdExtractor() {
        return new IIdExtractor<File>() {

            @Override
            public String getId(File element) {
                return "files";
            }
        };
    }

    static String NLM = "/home/pdendek/sample/CEDRAM/";
//    static String NLM = "/home/pdendek/sample/NUMDAM/";
    
    private static void proceedArgs(String[] args) {
    	for(String s : args){
    		if(s.split("=")[0].equals("NLM"))
    			NLM = s.split("=")[1];
    	}
	}
    
    @SuppressWarnings({ "static-access", "rawtypes", "unused", "unchecked" })
	public static void main(String[] args) throws Throwable{
    	
    	proceedArgs(args);
    	
    	_1MassiveFileIteratorBuilder o1 = new _1MassiveFileIteratorBuilder();
//    	_2MassiveYElementFromNLMNode o2 = new _2MassiveYElementFromNLMNode();
    	YToCatObjProcessingNode o3 = new YToCatObjProcessingNode();
    	_4MassiveEditorWritingNode o4 = new _4MassiveEditorWritingNode();
    	
    	int error 	=	0;
    	int i 		=	0;
    	Bwmeta2_0ToYTransformer transformer = new Bwmeta2_0ToYTransformer();
    	HashMap hm = new HashMap();
//    	hm.put(o1.AUX_PARAM_SOURCE_DIR, "/home/pdendek/sample/CEDRAM");
    	hm.put(o1.AUX_PARAM_SOURCE_DIR, NLM);
    	String[] ext = {"xml"}; 
    	o1.setExtensions(ext);
    	ISourceIterator<File> it = o1.build(hm);
    	NlmToYTransformer nlmToYransformer = new NlmToYTransformer();
	
    	long start = System.nanoTime();
    	
    	String fileStr = null;
    	
    	List<CatalogObject<String>> col = new LinkedList<CatalogObject<String>>();
    	
    	
    	System.out.println("!!!!!!!!!Zaczynam!!!!!!!!!");
    	while(it.hasNext()){
    	i++;
    		
    		File f = it.next();
//    		File f = new File("/home/pdendek/sample/CEDRAM/urn:math-thar.mathdoc.fr:CEDRAM:ACIRM_2009__1_1_41_0.xml");
    		System.out.println("\r"+i+"/"+it.getEstimatedSize()+"         "+f.getAbsolutePath());
    		

    		
//    		if(f.getAbsolutePath().equals("/home/pdendek/sample/CEDRAM/urn:math-thar.mathdoc.fr:CEDRAM:ACIRM_2009__1_1_41_0.xml")){
//    			System.out.println();
//    		}
    		
    		
    		char[] buf;
    		FileReader fr;
    				
    		try{
    			buf = new char[(int) f.length()];
        		fr = new FileReader(f);
        		fr.read(buf);
	    	    fileStr = new String(buf);
	
	    	    String[] array = {"p","italic","bold","monospace","underline","sup","sub","sans-serif"};
	    	    for(String s : array){
	    	    	fileStr = fileStr.replace("<"+s+">", "");
	    	    	fileStr = fileStr.replace("</"+s+">", "");
	    	    	fileStr = fileStr.replace("<"+s+"/>", "");
	    	    }
	    	    StringBuilder sb = new StringBuilder(fileStr);
//	    	    ,"sc","ext-link",
//	    	    {"source" - nie wiem co tam jest, zajrzyj do /home/pdendek/sample/NUMDAM/urn:math-thar.mathdoc.fr:NUMDAM:AIHPB_2008__44_2_362_0.xml
	    	    
	    	    String[] array2 = {"xref","uri","styled-content","inline-formula","list","notes"};
	    	    for(String s : array2)
	    	    	while(true){
		        		Matcher matcher1 = Pattern.compile("<"+s+".*?</"+s+">",Pattern.UNICODE_CASE|Pattern.CANON_EQ|Pattern.CASE_INSENSITIVE|Pattern.DOTALL).matcher(sb);
		        		if(!matcher1.find())break;
		        		sb.delete(matcher1.start(),matcher1.end());
		        	}
	            fileStr = sb.toString();
	            String[] array3 = {"xref"};
	    	    for(String s : array3)
	    	    	while(true){
		        		Matcher matcher1 = Pattern.compile("<"+s+".*?/>",Pattern.UNICODE_CASE|Pattern.CANON_EQ|Pattern.CASE_INSENSITIVE|Pattern.DOTALL).matcher(sb);
		        		if(!matcher1.find())break;
		        		sb.delete(matcher1.start(),matcher1.end());
		        	}

	            fileStr = sb.toString();
        		List<YExportable> yexl = nlmToYransformer.read(fileStr, (Object[]) null); 
        		List<YElement> yel = new LinkedList<YElement>(); 
        		
        		List<YExportable> lst = yexl;
//                YToBwmeta2_0Transformer t = new YToBwmeta2_0Transformer();
//                System.out.println(t.write(lst));
//                System.out.println("=========================");
//        		
        		
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
            	System.out.println("============================");
            	error++;
            }
    	}
    	System.out.println();
    	System.out.println();
    	System.out.println("File proceeded: "+i);
		System.out.println("Total number of errors: "+error);
		double err = error;
		err/=i;
		err*=100;
		System.out.println("Error rate: "+err+"%");
		System.out.println("Total time: "+((double)(System.nanoTime()-start)/(1000000000.0))+" sec.");
    	System.out.println();
    	System.out.println();
    	System.out.println("End of transmission");
    	
//    	DataSourcesFactory dsf = new DataSourcesFactory();
//    	dsf.set
//    	dsf.setPreferBwMeta2(true);
    	
    	
    	
    	
    	
//    	int j=0;
//    	List<YExportable> lex = null;
//    	for(CatalogObject<String> co : col){
////    		if(j>=12)break;
//    		
//    		
//    		j++;
//    	}
    }
      
    
}
