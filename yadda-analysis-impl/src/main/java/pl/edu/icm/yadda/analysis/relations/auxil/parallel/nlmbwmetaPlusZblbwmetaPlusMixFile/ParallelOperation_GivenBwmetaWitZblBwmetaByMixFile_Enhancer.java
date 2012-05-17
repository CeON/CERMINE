package pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlmbwmetaPlusZblbwmetaPlusMixFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Operation;
import pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta.ExtensionFileIteratorBuilder;
import pl.edu.icm.yadda.analysis.relations.auxil.statementbuilder.NameProceeder;
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
import pl.edu.icm.yadda.imports.zentralblatt.ZblFileToBwmetaTool;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectPart;

/**
 * 
 * 
 * @author pdendek (code refactoring by tkusm)
 * 
 */
public class ParallelOperation_GivenBwmetaWitZblBwmetaByMixFile_Enhancer implements Operation<File> {

    @SuppressWarnings("rawtypes")
    static IMetadataReader reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_2_0,
            BwmetaTransformers.Y);
    static Bwmeta2_0ToYTransformer transformer = new Bwmeta2_0ToYTransformer();
    static ExtensionFileIteratorBuilder o1 = new ExtensionFileIteratorBuilder();
    @SuppressWarnings("rawtypes")
    static HashMap hm = new HashMap();
    static YToCatObjProcessingNode o3 = new YToCatObjProcessingNode();

    static String ENHANCE_NLM_OUT_DIR = "/home/pdendek/ENHANCED_2012/";
    static String NLM_IN_DIR = "/home/pdendek/sample/CEDRAM/";
    static String ZBL_IN_DIR = "/tmp/DST_ZBL23472565045672";
    static String MIX_IN_FILE = "/home/pdendek/MIX.txt";

    /**
     * Generates two things: 1) path to the file containing zbl-bwmeta xml of
     * given zbl-identity. 2) path to the file that maps 10-letters-zbl-id to
     * 8-letter-zbl-id
     * 
     * @author tkusm
     * 
     */
    public interface ZblBwMetaPathsGenerator {
        public String generateZblBwmetaPath(String zblId);
        public String getExtZblToZblMappingFilePath();
    }

    public ParallelOperation_GivenBwmetaWitZblBwmetaByMixFile_Enhancer(String[] args) {
        proceedArgs(args);
    }

    private static void proceedArgs(String[] args) {
        for (int i = 0; i + 1 < args.length; i += 2) {
            String s = args[i];
            if (s.equals("NLM"))
                NLM_IN_DIR = args[i + 1];
            else if (s.equals("ENHANCE_NLM"))
                ENHANCE_NLM_OUT_DIR = args[i + 1];
            else if (s.equals("ZBL"))
                ZBL_IN_DIR = args[i + 1];
            else if (s.equals("MIX"))
                MIX_IN_FILE = args[i + 1];
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////

    @Override
    public void perform(File fin) {
        try {
            String outpath = ENHANCE_NLM_OUT_DIR + fin.getName().substring(0, fin.getName().length() - 4)
                    + "enhanced.xml";
            File fout = new File(outpath);
            if (fout.exists())
                return;

            enhanceBwMetaFile(fin, fout, new ZblBwMetaPathsGenerator() {
                @Override
                public String generateZblBwmetaPath(String zblId) {
                    return ZBL_IN_DIR + ZblFileToBwmetaTool.generateBwMetaFileName(zblId);
                }

                @Override
                public String getExtZblToZblMappingFilePath() {
                    return MIX_IN_FILE;
                }
            });

        } catch (Exception e) {
            try {
                System.err.println("Following exception occurred in file: " + fin.getAbsolutePath());
            } catch (Exception NullPointerException) {
                System.err.println("Following exception occurred in file: >>Null<<");
            }
            e.printStackTrace();
        }
    }

    /**
     * Reads bwmemta from file fin, enhances yelements with zbl data, and stores
     * to fout. zblPathGenerator is used to find matching zbl-bwmeta-xml file
     * path.
     */
    public void enhanceBwMetaFile(File fin, File fout, ZblBwMetaPathsGenerator zblPathGenerator) throws Exception,
            FileNotFoundException, UnsupportedEncodingException, TransformationException, IOException {

        List<YElement> lye = loadYElements(fin);

        for (YElement ye : lye) {
            enhanceYElement(ye, zblPathGenerator);
            enhanceRelationsInYElement(ye, zblPathGenerator);
        }

        storeYElements(fout, lye);
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////

    protected void enhanceYElement(YElement ye, ZblBwMetaPathsGenerator zblPathGenerator) throws Exception {
        // only articles are considered as main elements
        if (!ye.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).equals(YConstants.EXT_LEVEL_JOURNAL_ARTICLE)) {
            return;
        }

        // sprawdź czy główny dokument ma id Zblattowe
        String mainZblExtId = retrieveYElementZblId(ye);

        // jak ma to skonsumuj tę informację
        if (mainZblExtId != null) {
            String zblId = mapExtZblToZbl(mainZblExtId, zblPathGenerator.getExtZblToZblMappingFilePath());
            if (zblId != null) {
                List<YElement> zblyel = loadZblYElements(zblId, zblPathGenerator);
                enhanceArticleElement(ye, zblyel);
            }
        }
    }

    protected void enhanceRelationsInYElement(YElement ye, ZblBwMetaPathsGenerator zblPathGenerator) throws Exception {
        // sprawdź czy referencje głównego dokumentu mają id Zblattowe
        for (YRelation yr : ye.getRelations()) {
            if ("reference-to".equals(yr.getType()) || "related-to".equals(yr.getType())) {
                String extZblId = yr.getOneAttributeSimpleValue("reference-parsed-id-zbl");
                if (extZblId != null) {
                    String zblId = mapExtZblToZbl(extZblId, zblPathGenerator.getExtZblToZblMappingFilePath());
                    if (zblId != null) {
                        List<YElement> zblyel = loadZblYElements(zblId, zblPathGenerator);
                        if (zblyel.size() != 0)
                            enhanceRelationElement(yr, zblyel);
                    }
                }
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////

    private static void enhanceArticleElement(YElement ye, List<YElement> zblyel) {

        for (YElement zblye : zblyel) {
            updateYElementCategoryRefWithZblCategoryRefs(ye, zblye);
            updateYElementContributorsWithZblContributors(ye, zblye);
            updateYElementIdsWithZblIds(ye, zblye);
            updateYElementTagsWithZblTags(ye, zblye);
        }
    }

    private static void updateYElementTagsWithZblTags(YElement ye, YElement zblye) {
        for (YTagList tl : zblye.getTagLists())
            ye.addTagList(tl);
    }

    private static void updateYElementIdsWithZblIds(YElement ye, YElement zblye) {
        for (YId i : zblye.getIds())
            if (ye.getId(i.getScheme()) == null)
                ye.addId(i);
    }

    private static void updateYElementCategoryRefWithZblCategoryRefs(YElement ye, YElement zblye) {
        for (YCategoryRef r : zblye.getCategoryRefs())
            if (!ye.getCategoryRefs().contains(r))
                ye.addCategoryRef(r);
    }

    private static void updateYElementContributorsWithZblContributors(YElement ye, YElement zblye) {
    	for (YContributor zblc : zblye.getContributors()) {
    		ArrayList<YName> outZblNames = new ArrayList<YName>();
    		for(YName n : zblc.getNames()){
    			if ("canonical".equals(n.getType())){
    				n.setText(NameProceeder.unifyCanonical(n.getText()));
    			}else if ("forenames".equals(n.getType())){
    				n.setText(NameProceeder.unifyForenames(n.getText()));
    			}else if ("forename".equals(n.getType())){
    				n.setText(NameProceeder.unifyForenames(n.getText()));
    			}else if ("surname".equals(n.getType())){
    				n.setText(NameProceeder.unifySurname(n.getText()));
    			}
    			outZblNames.add(n);
    		}
    		zblc.setNames(outZblNames);
    	}
    	
    	for (YContributor nc : ye.getContributors()) {
    		ArrayList<YName> outZblNames = new ArrayList<YName>();
    		for(YName n : nc.getNames()){
    			if ("canonical".equals(n.getType())){
    				n.setText(NameProceeder.unifyCanonical(n.getText()));
    			}else if ("forenames".equals(n.getType())){
    				n.setText(NameProceeder.unifyForenames(n.getText()));
    			}else if ("forename".equals(n.getType())){
    				n.setText(NameProceeder.unifyForenames(n.getText()));
    			}else if ("surname".equals(n.getType())){
    				n.setText(NameProceeder.unifySurname(n.getText()));
    			}
    			outZblNames.add(n);
    		}
    		nc.setNames(outZblNames);
    	}
    	
    	
        for (YContributor zblc : zblye.getContributors()) {
            for (YContributor nc : ye.getContributors()) {
                // FIXME how many times occurs collision in a contributors'
                // surnames?
                // what shall we do with "Smith J." and "Smith J. Jr." or
                // basically "Smith J." and "Smith M." in this phase?
                // because for
                // NLM "Jason P. Bell":"Jason P.","Bell"
                // ZBL is "Jason Bell":"Jason","Bell"
                String sname = null;
                for (YName name : nc.getNames()) {
                    if ("surname".equals(name.getType()))
                        sname = name.getText();
                }

                if (sname != null && zblc.getOneName("surname") != null
                        && sname.equals(zblc.getOneName("surname").getText())) {
                    for (YAttribute a : zblc.getAttributes(YConstants.AT_ZBL_AUTHOR_FINGERPRINT)) {
                        nc.addAttribute(a);
                    }

                }
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // -----------------------------------------------------------------------------------

    private static void enhanceRelationElement(YRelation relye, List<YElement> zblyel) {

        for (YElement zblye : zblyel) {
            updateYRelationWithZblClassificationCodes(relye, zblye);

            for (YContributor c : zblye.getContributors()) {
                updateYRelationWithZblContributor(relye, c);
            }

            updateYRelationWithZblIds(relye, zblye);

            // FIXME please please find what activity it actually should do...
            // for(YId i : zblye.getIds()){
            // if(i.getScheme().equals(YConstants.EXT_SCHEME_ISSN)){
            // ya = new
            // YAttribute(YConstants.AT_REFERENCE_PARSED_ID_ISSN,i.getValue());
            // relye.addAttribute(ya);
            // } else if(i.getScheme().equals(YConstants.EXT_SCHEME_ISBN)){
            // ya = new
            // YAttribute(YConstants.AT_REFERENCE_PARSED_ID_ISBN,i.getValue());
            // relye.addAttribute(ya);
            // } else if(i.getScheme().equals(YConstants.EXT_SCHEME_ZBL)){
            // ya = new
            // YAttribute(YConstants.AT_REFERENCE_PARSED_ID_ZBL,i.getValue());
            // relye.addAttribute(ya);
            // }
            // }

            updateYRelationWithZblNames(relye, zblye);
            updateYRelationWithZblTags(relye, zblye);
        }
    }

    public static void updateYRelationWithZblTags(YRelation relye, YElement zblye) {
        for (YTagList tl : zblye.getTagLists()) {
            YAttribute ya = new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_TAG, "");
            ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_TYPE, tl.getType()));
            ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_LANGUAGE, tl.getLanguage().getName()));

            for (String t : tl.getValues()) {
                ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_VALUE, t));
            }
            relye.addAttribute(ya);
        }
    }

    public static void updateYRelationWithZblNames(YRelation relye, YElement zblye) {
        for (YName n : zblye.getNames()) {
            YAttribute ya = new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_NAME, "");
            ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_LANGUAGE, n.getLanguage().getName()));
            ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_TYPE, n.getType()));
            ya.addAttribute(new YAttribute(YConstants.AT_ENHANCED_FROM_ZBL_VALUE, n.getText()));
            relye.addAttribute(ya);
        }
    }

    public static void updateYRelationWithZblIds(YRelation relye, YElement zblye) {
        YAttribute ya = new YAttribute(YConstants.TG_CATEGORY, "");
        for (YId i : zblye.getIds()) {
            if (i.getScheme().equals(YConstants.EXT_SCHEME_ISSN)) {
                ya.addAttribute(new YAttribute(YConstants.EXT_SCHEME_ISSN, i.getValue()));
            } else if (i.getScheme().equals(YConstants.EXT_SCHEME_ISBN)) {
                ya.addAttribute(new YAttribute(YConstants.EXT_SCHEME_ISBN, i.getValue()));
            } else if (i.getScheme().equals(YConstants.EXT_SCHEME_ZBL)) {
                ya.addAttribute(new YAttribute(YConstants.EXT_SCHEME_ZBL, i.getValue()));
            } else if (i.getScheme().equals(YConstants.EXT_SCHEME_ZBL)) {
                ya.addAttribute(new YAttribute(YConstants.EXT_SCHEME_ZBL, i.getValue()));
            }
        }
        relye.addAttribute(ya);
    }

    @SuppressWarnings("deprecation")
	public static void updateYRelationWithZblContributor(YRelation relye, YContributor c) {
        YAttribute ya = null;
        if (c.getOneName("canonical") != null)
            ya = new YAttribute(YConstants.AT_REFERENCE_PARSED_AUTHOR, c.getOneName("canonical").getText().toString());
        if (c.getAttributes(YConstants.AT_ZBL_AUTHOR_FINGERPRINT).size() > 0)
            ya.addAttribute(YConstants.AT_ZBL_AUTHOR_FINGERPRINT, c.getAttributes(YConstants.AT_ZBL_AUTHOR_FINGERPRINT)
                    .get(0).getValue());
        if (c.getOneName("forenames") != null)
            ya.addAttribute(YConstants.AT_REFERENCE_PARSED_AUTHOR_FORENAMES, c.getOneName("forenames").getText()
                    .toString());
        ya.addAttribute(YConstants.AT_REFERENCE_PARSED_AUTHOR_SURNAME, c.getOneName("surname").getText().toString());
        relye.addAttribute(ya);
    }

    public static void updateYRelationWithZblClassificationCodes(YRelation relye, YElement zblye) {
        YAttribute ya;
        ya = new YAttribute(YConstants.TG_CATEGORY, "");
        for (YCategoryRef r : zblye.getCategoryRefs()) {
            ya.addAttribute(new YAttribute(r.getClassification(), r.getCode()));
        }
        relye.addAttribute(ya);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Takes 10-letters-zbl-ext-id and returns 8-letters-zbl-id.
     * 
     * @param extZblId
     *            10-letters-zbl-ext-id
     * @param mappingFilePath
     *            path to the file that contains mapping (every line in format:
     *            AAAAAAAA BBBB.BBBBB for example 00000932 0767.93061)
     * @return returns 8-letters-zbl-id
     * @throws Exception
     */
    public static String mapExtZblToZbl(String extZblId, String mappingFilePath) throws Exception {
        if (extZblId == null || extZblId.length() != 10)
            return null;
        File fmix = new File(mappingFilePath);
        MixFileIteratorBuilder o2 = new MixFileIteratorBuilder(fmix);
        ISourceIterator<MixRecord> it = o2.build(null);
        while (it.hasNext()) {
            MixRecord mr = it.next();
            if (extZblId.equals(mr.getDotId())) {
                it.clean();
                it = null;
                fmix = null;
                return mr.get10DigitId();
            }

        }
        it.clean();
        it = null;
        fmix = null;
        return null;
    }

    public static String retrieveYElementZblId(YElement ye) {
        String mainZblExtId = null;
        for (YId yi : ye.getIds()) {
            if (YConstants.EXT_SCHEME_ZBL.equals(yi.getScheme())) {
                mainZblExtId = yi.getValue();
                break;
            }
        }
        return mainZblExtId;
    }

    public static List<YElement> loadZblYElements(String zblId, ZblBwMetaPathsGenerator zblPathGenerator)
            throws Exception {
        String zblFilePath = zblPathGenerator.generateZblBwmetaPath(zblId);
        File zblFile = new File(zblFilePath);
        if (!zblFile.exists()) {
            zblFile = null;
            return Collections.emptyList();
        }

        return loadYElements(zblFile);
    }

    public static List<YElement> loadYElements(File f) throws Exception {
        FileReader fr = new FileReader(f);
        @SuppressWarnings("unchecked")
		List<YExportable> lye = reader.read(fr, (Object[])null);
        fr.close();
        fr = null;
        List<YElement> yel = new LinkedList<YElement>();
        for (YExportable ye : lye)
            if (ye instanceof YElement)
                yel.add((YElement) ye);
        return yel;
    }

    public static void storeYElements(File fout, List<YElement> lye) throws FileNotFoundException,
            UnsupportedEncodingException, TransformationException, IOException {
        FileOutputStream fos = new FileOutputStream(fout);
        OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
        for (CatalogObject<String> co : o3.process(lye, null)) {
            for (CatalogObjectPart<String> cop : co.getParts()) {
                out.write(cop.getData());
                out.flush();
            }
        }
        out.close();
        fos.close();
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

    // //////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////

}
