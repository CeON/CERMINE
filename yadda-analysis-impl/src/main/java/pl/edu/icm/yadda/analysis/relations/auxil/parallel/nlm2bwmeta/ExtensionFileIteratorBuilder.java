
package pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Parallel;
import pl.edu.icm.yadda.analysis.relations.auxil.trash.YToCatObjProcessingNode;
import pl.edu.icm.yadda.analysis.zentralblatteudmlmixer.MixFileIteratorBuilder;
import pl.edu.icm.yadda.analysis.zentralblatteudmlmixer.auxil.MixRecord;
import pl.edu.icm.yadda.bwmeta.model.YAttribute;
import pl.edu.icm.yadda.bwmeta.model.YCategoryRef;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.model.YId;
import pl.edu.icm.yadda.bwmeta.model.YName;
import pl.edu.icm.yadda.bwmeta.model.YRelation;
import pl.edu.icm.yadda.bwmeta.model.YTagList;
import pl.edu.icm.yadda.bwmeta.transformers.Bwmeta2_0ToYTransformer;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
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
public class ExtensionFileIteratorBuilder implements ISourceIteratorBuilder<File> {

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

    static IMetadataReader reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_2_0, BwmetaTransformers.Y);
    static Bwmeta2_0ToYTransformer transformer = new Bwmeta2_0ToYTransformer();
    static ExtensionFileIteratorBuilder o1 = new ExtensionFileIteratorBuilder();
    static HashMap hm = new HashMap();
    static YToCatObjProcessingNode o3 = new YToCatObjProcessingNode();
    
    static String ENHANCE_NLM = "/home/pdendek/sample/ENHANCE_2/";
    static String NLM = "/home/pdendek/sample/CEDRAM/";
//    static String NLM = "/home/pdendek/sample/NUMDAM/";
    static String ZBL = "/home/pdendek/sample/ZBL/";
    static String MIX = "/home/pdendek/MIX.txt";
    	
    @SuppressWarnings({ "static-access", "rawtypes", "unused", "unchecked" })
	public static void main(String[] args) throws Throwable{
    	
    	proceedArgs(args);
    	
    	int error 	=	0;
    	int i 		=	1;
    
    	if(!(new File(ENHANCE_NLM).exists()))
			new File(ENHANCE_NLM).mkdirs();
    	
    	String[] ext = {"5.bwmeta.xml"}; 
    	//create bwmeta from nlm iterator 
    	hm.put(o1.AUX_PARAM_SOURCE_DIR, NLM);  	
    	o1.setExtensions(ext);
    	ISourceIterator<File> itnlm = o1.build(hm);
    	//create bwmeta from zbl iterator
//    	hm.clear();
//    	ext[0] = "xml"; 
//    	hm.put(o1.AUX_PARAM_SOURCE_DIR, ZBL);  	 
//    	o1.setExtensions(ext);
//    	ISourceIterator<File> itzbl = o1.build(hm);
    	
    	long start = System.nanoTime();
    	String fileStr = null;
    	
    	System.out.println("Przetworze teraz "+itnlm.getEstimatedSize()+" obiektów CEDRAMowych w formacie BWMETA");
    	
    	File f = null;
    	
    	LinkedList<File> flist = new LinkedList<File>();
    	while(itnlm.hasNext()) flist.add(itnlm.next());
    	
    	Parallel parallel = new Parallel();
    	parallel.For(flist, null);
    	
    	for(;itnlm.hasNext() && i<100;i++){
    		try{
        		f = itnlm.next();
        		System.out.println("\r"+i+"/"+itnlm.getEstimatedSize()+"     "+f.getAbsolutePath());
        		List<YElement> lye = toEnhance(f);
        		for(YElement ye : lye){
        			//sprawdź czy główny dokument ma id Zblattowe
        			String mainZblExtId = null;
        			for(YId yi : ye.getIds()){
        				if(YConstants.EXT_SCHEME_ZBL.equals(yi.getScheme())){
        					mainZblExtId = yi.getValue();
        					break;
        				}
        			}
        			//jak ma to skonsumuj tę informację
        			if(mainZblExtId!=null){
        				String zblId = mapExtZblToZbl(mainZblExtId);
        				if(zblId!=null){
        					List<YElement> zblyel = getZblData(zblId); 
        					enhanceArticleElement(ye, zblyel);
        				}
        			}else;
        			//sprawdź czy referencje głównego dokumentu mają id Zblattowe
        			for(YRelation yr : ye.getRelations()){
        				if("reference-to".equals(yr.getType()) || "related-to".equals(yr.getType())){
        					String extZblId = yr.getOneAttributeSimpleValue("reference-parsed-id-zbl");
        					if(extZblId!=null){
        						String zblId = mapExtZblToZbl(extZblId);
            					if(zblId!=null){
                					List<YElement> zblyel = getZblData(zblId);
                					if(zblyel.size()!=0)
                						enhanceRelationElement(yr, zblyel);
                				}
        					}
        				}else;
        			}
        		}
        		String path = ENHANCE_NLM + f.getName().substring(0, f.getName().length()-4)+"enhanced.xml";
        		File fout = new File(path);
        		if(fout.exists()){
        			fout.delete();
        			fout.createNewFile();
        		}
        		
        		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fout),"UTF-8");
    			for(CatalogObject<String> co : o3.process(lye, null)){
        			for(CatalogObjectPart<String> cop : co.getParts()){
        				out.write(cop.getData());
        				out.flush();
            		}
            	}
				out.close();
        		
        		long end = System.nanoTime();
            	System.out.println("Time till now: "+((double)(end-start)/(1000000000.0))+" sec.");
            	System.out.println("Time till now per BWMETA(CEDRAM) file: "+((double)(end-start)/(i*1000000000.0))+" sec.");
        	}catch(Exception e){
        		System.out.println("Following exception occurred in file: "+f.getAbsolutePath());
        		e.printStackTrace();
        	}
    	}
    	long end = System.nanoTime();
    	System.out.println("Total time: "+((double)(end-start)/(1000000000.0))+" sec.");
    	System.out.println("Time per BWMETA(CEDRAM) file: "+((double)(end-start)/(i*1000000000.0))+" sec.");
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

	/*
   <relation type="reference-to">
      <attribute key="reference-parsed-id-mr">
        <value>1744913</value>
      </attribute>
      <attribute key="reference-parsed-id-zbl">
        <value>0951.11551</value>
      </attribute>
      <attribute key="reference-parsed-type">
        <value>book</value>
      </attribute>
      <attribute key="reference-text">
        <value>S. Akiyama, The dynamical norm conjecture and Pisot tilings, Sūrikaisekikenkyūsho Kōkyūroku (1999), no. 1091, 241–250.</value>
      </attribute>
    </relation>
     */
    
	private static void enhanceRelationElement(YRelation relye,
			List<YElement> zblyel) {
		
//		relye.add
		
		YAttribute ya = null;
		
		for(YElement zblye : zblyel){
			ya = new YAttribute(YConstants.TG_CATEGORY,"");
			for(YCategoryRef r : zblye.getCategoryRefs()){
				ya.addAttribute(new YAttribute(r.getClassification(), r.getCode()));
			}
			relye.addAttribute(ya);

			for(YContributor c : zblye.getContributors()){
				if(c.getOneName("canonical")!=null)
					ya = new YAttribute(YConstants.AT_REFERENCE_PARSED_AUTHOR,c.getOneName("canonical").getText().toString());
				if(c.getAttributes(YConstants.AT_ZBL_AUTHOR_FINGERPRINT).size()>0)
					ya.addAttribute(YConstants.AT_ZBL_AUTHOR_FINGERPRINT , c.getAttributes(YConstants.AT_ZBL_AUTHOR_FINGERPRINT).get(0).getValue());
				if(c.getOneName("forenames")!=null)
					ya.addAttribute(YConstants.AT_REFERENCE_PARSED_AUTHOR_FORENAMES , c.getOneName("forenames").getText().toString() );
				ya.addAttribute(YConstants.AT_REFERENCE_PARSED_AUTHOR_SURNAME , c.getOneName("surname").getText().toString() );
				relye.addAttribute(ya);
			}
			
			ya = new YAttribute(YConstants.TG_CATEGORY,"");
			for(YId i : zblye.getIds()){
				if(i.getScheme().equals(YConstants.EXT_SCHEME_ISSN)){
					ya.addAttribute(new YAttribute(YConstants.EXT_SCHEME_ISSN, i.getValue()));
				}else if(i.getScheme().equals(YConstants.EXT_SCHEME_ISBN)){
					ya.addAttribute(new YAttribute(YConstants.EXT_SCHEME_ISBN, i.getValue()));
				} else if(i.getScheme().equals(YConstants.EXT_SCHEME_ZBL)){
					ya.addAttribute(new YAttribute(YConstants.EXT_SCHEME_ZBL, i.getValue()));
				} else if(i.getScheme().equals(YConstants.EXT_SCHEME_ZBL)){
					ya.addAttribute(new YAttribute(YConstants.EXT_SCHEME_ZBL, i.getValue()));
				}
			}
			relye.addAttribute(ya);
			
			
			for(YId i : zblye.getIds()){
				if(i.getScheme().equals(YConstants.EXT_SCHEME_ISSN)){
					ya = new YAttribute(YConstants.AT_REFERENCE_PARSED_ID_ISSN,i.getValue());
					relye.addAttribute(ya);
				} else if(i.getScheme().equals(YConstants.EXT_SCHEME_ISBN)){
					ya = new YAttribute(YConstants.AT_REFERENCE_PARSED_ID_ISBN,i.getValue());
					relye.addAttribute(ya);
				} else if(i.getScheme().equals(YConstants.EXT_SCHEME_ZBL)){
					ya = new YAttribute(YConstants.AT_REFERENCE_PARSED_ID_ZBL,i.getValue());
					relye.addAttribute(ya);
				}
			}
			
			
			for(YName n : zblye.getNames()){
				ya = new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_NAME,"");
				ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_LANGUAGE,n.getLanguage().getName()));
				ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_TYPE,n.getType()));
				ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_VALUE,n.getText()));
				relye.addAttribute(ya);
			}
				
			for(YTagList tl : zblye.getTagLists()){
				ya = new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_TAG, "");
				ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_TYPE, tl.getType()));
				ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_LANGUAGE, tl.getLanguage().getName()));
				
				for(String t : tl.getValues()){
					ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_VALUE,t));
				}
				relye.addAttribute(ya);
			}
		}
	}

	/*		dodatkowe istniejące pola, które możliwe, że występują przy tłumaczniu
			for(YAffiliation a : zblye.getAffiliations())
				relye.addAffiliation(a);
			for(YAttribute a : zblye.getAttributes())
				relye.addAttribute(a);
			for(YContentEntry c : zblye.getContents())
				relye.addContent(c);
			for(YDate d : zblye.getDates())
				relye.addDate(d);
			for(YDescription d : zblye.getDescriptions())
				relye.addDescription(d);
			for(YLanguage l : zblye.getLanguages())
				relye.addLanguage(l);
			for(YName n : zblye.getNames())
				ye.addName(n);			
			for(YStructure s : zblye.getStructures())
				ye.addStructure(s);				 
	 */
	private static void enhanceArticleElement(YElement ye, List<YElement> zblyel) {
		for(YElement zblye : zblyel){
			for(YCategoryRef r : zblye.getCategoryRefs())
				if(!ye.getCategoryRefs().contains(r))
					ye.addCategoryRef(r);
			
			LinkedList<YContributor> ycl = new LinkedList<YContributor>();
			for(YContributor zblc : zblye.getContributors()){
				for(YContributor nc : ye.getContributors()){
					//FIXME how many times occurs collision in a contributors' surnames? 
					//what shall we do with "Smith J." and "Smith J. Jr." or basically "Smith J." and "Smith M." in this phase?
					//because for 
					//NLM "Jason P. Bell":"Jason P.","Bell" 
					//ZBL is "Jason Bell":"Jason","Bell"
					String sname = null;
					String fname = null;
					String cname = null;
					for(YName name : nc.getNames()){
						if("canonical".equals(name.getType())) cname = name.getText();
						else if("forenames".equals(name.getType())) fname = name.getText();
						else if("surname".equals(name.getType())) sname = name.getText();
					}
					
					if(sname!=null && zblc.getOneName("surname")!=null && sname.equals(zblc.getOneName("surname").getText())){
						if(zblc.getOneName("forenames").getText().split(" ").length >fname.split(" ").length){
							fname = zblc.getOneName("forenames").getText();
							YName fn = new YName(fname);
							fn.setType("forenames");
							YName sn = new YName(sname);
							sn.setType("surname");
							YName cn;
							if(cname!=null) cn = new YName(cname);
							else cn = new YName(fname+" "+sname);
							cn.setType("canonical");
							LinkedList<YName> ynames = new LinkedList<YName>();
							ynames.add(fn);
							ynames.add(sn);
							ynames.add(cn);
							nc.setNames(ynames);
						} 
						for(YAttribute a : zblc.getAttributes(YConstants.AT_ZBL_AUTHOR_FINGERPRINT)){
							nc.addAttribute(a);
						}
							
					}else;
				}
			}
			for(YId i : zblye.getIds())
				if(ye.getId(i.getScheme())==null)
					ye.addId(i);
			for(YTagList tl : zblye.getTagLists())
				ye.addTagList(tl);
		}
	}

	private static List<YElement> getZblData(String zblId) throws Exception {
		String zblFilePath = ZBL + "Zbl"+zblId+".bwmeta.xml";
		File zblFile = new File(zblFilePath);
		if(!zblFile.exists()) return Collections.EMPTY_LIST;
		FileReader fr = new FileReader(zblFile);
		List<YExportable> lye = reader.read(fr, null);
		List<YElement> yel = new LinkedList<YElement>();
		for(YExportable ye : lye)
			if(ye instanceof YElement) yel.add((YElement)ye);
		return yel;
	}

	private static String mapExtZblToZbl(String extZblId) throws Exception {
		if(extZblId==null || extZblId.length()!=10) return null;
		MixFileIteratorBuilder o2 = new MixFileIteratorBuilder(new File(MIX));
		ISourceIterator<MixRecord> it = o2.build(null);
		while(it.hasNext()){
			MixRecord mr = it.next();
			if(extZblId.equals(mr.getDotId())) {
				it.clean();
				return mr.get10DigitId();
			}
				 
		} 
		it.clean();
		return null;
	}

	private static List<YElement> toEnhance(File f) throws Exception {
		char[] buf;
		buf = new char[(int)f.length()];
		FileReader fr = new FileReader(f);
		List<YExportable> lye = reader.read(fr, null);
		List<YElement> yel = new LinkedList<YElement>();
		for(YExportable ye : lye)
			if(ye instanceof YElement) yel.add((YElement)ye);
		return yel;
	}
}
