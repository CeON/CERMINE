package pl.edu.icm.yadda.analysis.mscsimilarity;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.schlichtherle.io.FileOutputStream;

import pl.edu.icm.yadda.analysis.datastructures.SymmetricTreeMapMatrix;
import pl.edu.icm.yadda.analysis.datastructures.TreeMapMatrix;
import pl.edu.icm.yadda.bwmeta.model.YAttribute;
import pl.edu.icm.yadda.bwmeta.model.YCategoryRef;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IInitializableFinalizableNode;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * Calculates how many times an author occurs in different MSC classes and then calculates number
 * of authors that occur in pairs of MSC classes (builds matrix class x class -> number of common authors).
 * 
 * 
 * Zlicza ilu autorów występuje zarówno w jednej jak i w drugiej klasie.
 * Jeżeli autor występuje zarówno w jednej klasie jak i w drugiej to dajemy +1.
 * TODO W przyszłości można jakoś uwzględnić, że dany autor wystąpił w klasie A 10 razy a w B 3 razy.
 * Na przekątnej liczba unikalnych autorów w danej klasie.
 * UWAGA: może się zdarzyć tak że jest wiele klasyfikacji w jednym dokumencie i wielu autorów w tym dokumencie
 * W takim przypadku każdej parze klas MSC w tym dokumencie zwiększa się liczba autorów o wszystkich tych którzy byli w tym dokumencie
 * Zakłada się, że wspólne występowanie klas jest inną cechą.  
 * 
 * @author tkusm
 *
 */
public class MscAuthorsCoocurenceStatsWritingNode implements IWriterNode<List<YElement>>, IInitializableFinalizableNode {

    /**
     * Logger.
     */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @key MSC class code
     * @value list of authors + number of occurrences in class      
     */
    protected Map<String, Map<String, Integer> > authorsInClass = new HashMap<String, Map<String, Integer>>();

    /**
     * @key author 
     * @value list of classes + number of occurrences in class
     */
    protected Map<String, Map<String, Integer> > classesOfAuthor = new HashMap<String, Map<String, Integer>>();
    
    /**
     * What's the name of the parameter that says where to store output matrix.
     */
    public static final String AUX_PARAM_OUTPUT_PATH = "acopath";
    /**
     * What's the name of the parameter that says what value should be put into empty places in output matrix.
     */
    public static final String AUX_PARAM_DEFAULT_VALUE = "adefval";

    @Override
    public void initialize(ProcessContext ctx) throws Exception {
    }

    @Override
    public void store(List<YElement> yelements, ProcessContext ctx) throws Exception {
        synchronized (this) {
            log.info("yelements.size() = {}", yelements.size());
            for (YElement yelement : yelements) {
                log.info("updating with next yelement = [{}]", yelement);
                updateStats(yelement);
            } // foreach in yelements
        }
    }

    private void updateStats(YElement yelement) {
        List<String> mscRefs = extractMscRefs(yelement.getCategoryRefs());
        List<String> fingerprints = extractAuthorsFingerprints(yelement.getContributors());
        if (mscRefs.size() == 0 || fingerprints.size() == 0) {
            return;
        }
        log.info("analyzing next yelement = [{}]", yelement);
        
        //kazdemu autorowi dodajemy jedno wystapienie w danej klasie
        //kazdej klasie dodajemy jedno wystapienie danego autora
        for (String authorFingerprint: fingerprints) {
            for (String mscRef: mscRefs) {
                
                increment(authorsInClass, mscRef, authorFingerprint);
                increment(classesOfAuthor, authorFingerprint, mscRef);
            }
        }
        
    }

    //---------------------------------------------------------------------------------------
    
    /**
     * Does: map[key1, key2] := map[key1, key2] + 1.
     * 
     * @param map
     * @param key1
     * @param key2
     */
    private static void increment(Map<String, Map<String, Integer> > map, String key1, String key2) {
        if (!map.containsKey(key1)) {
            map.put(key1, new TreeMap<String, Integer>());
        }
        Map<String, Integer> map2 = map.get(key1);
        if (!map2.containsKey(key2)) { //new entry
            map2.put(key2, 1); 
        } else { //increment previous value
            int previousValue = map2.get(key2);
            map2.put(key2, previousValue+1);
        }
        
        //System.out.println("[MscAuthorsCoocurenceStatsWritingNode] map["+key1+"]["+key2+"]++ -> "+map.get(key1).get(key2));
    }
    
    /**
     * 
     * @param contributors all contributors
     * @return contributors' zbl fingerprints
     */
    private List<String> extractAuthorsFingerprints(List<YContributor> contributors) {
        List<String> fingerprints = new ArrayList<String>();
        for (YContributor contributor: contributors) {
            List<YAttribute> attrs = contributor.getAttributes(YConstants.AT_ZBL_AUTHOR_FINGERPRINT);
            if (attrs.size() == 0) {
                continue;
            }
            if (attrs.size() > 1) {
                log.warn("More than one author-identifier(fingerprint) found for contributor=[{}]",contributor);                
            }
            fingerprints.add(attrs.get(0).getValue());
        }
        return fingerprints;
    }

    /**
     * @param refsa
     *            all the category references.
     * @return category references of Msc classification.
     */
    private List<String> extractMscRefs(List<YCategoryRef> refs) {
        List<String> mscRefs = new ArrayList<String>();

        for (YCategoryRef ref : refs) {
            if (ref.getClassification().equals(YConstants.EXT_CLASSIFICATION_MSC)) {
                mscRefs.add(ref.getCode());
            }
        }
        return mscRefs;
    }
    
    
    //---------------------------------------------------------------------------------------

    @Override
    public void finalize(ProcessContext ctx) throws Exception {
        try {
            log.info("Num classes={} Num authors={}", authorsInClass.size(), classesOfAuthor.size());
            log.info("Building co-occurrence matrix for authors in classes.");        
            SymmetricTreeMapMatrix<String, Integer> cooccurrence = buildCoocurrenceMatrix();
            
            String path = getOutputPath(ctx);
            log.info("Writing matrix to file [{}].", path);
            FileOutputStream fos = new FileOutputStream(path);
            TreeMapMatrix.storePlainText(cooccurrence, fos, TreeMapMatrix.SUGGESTED_SEPARATOR, getMatrixDefaultValue(ctx));
        } catch (Exception e) {
            log.error("MscAuthorsCoocurence Exception: "+e.getMessage());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(stream));
            log.error("MscAuthorsCoocurence Exception StackTrace: "+stream.toString());
        } catch (Throwable t) {
            log.error("MscAuthorsCoocurence Throwable: "+t.getMessage());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(stream));
            log.error("MscAuthorsCoocurence Throwable StackTrace: "+stream.toString());
        }
    }

    private String getOutputPath(ProcessContext ctx) {
        String path = (String) ctx.getAuxParam(AUX_PARAM_OUTPUT_PATH);
        if (path == null || path.length() <=0) {
            log.error("Process parameter [{}] must be set!", AUX_PARAM_OUTPUT_PATH);
        }
        return path;
    }
    
    private Integer getMatrixDefaultValue(ProcessContext ctx) {
        try {
            return Integer.parseInt( (String)ctx.getAuxParam(AUX_PARAM_DEFAULT_VALUE) );
        } catch (Exception e) {            
        }
        log.info("no default for missing values in output matrix");
        return null;
    }

    /**
     * Symmetric matrix of type MscClassCode x MscClassCode -> number of
     * authors (single author = 1) co-occurrences. 
     * @key MSC class code
     * @value number of co-occurrences
     */
    public SymmetricTreeMapMatrix<String, Integer> buildCoocurrenceMatrix() {
        SymmetricTreeMapMatrix<String, Integer> cooccurrence = new SymmetricTreeMapMatrix<String, Integer>();        

        log.info("[buildCoocurrenceMatrix] Entering.");
        List<String> cc = new ArrayList<String>();  //category codes        
        for (String mscCode1: authorsInClass.keySet()) {
            //System.out.println("LOOP1----------------------------------------------------"+mscCode1+" ");
            cc.add(mscCode1);
            Map<String, Integer> authors = authorsInClass.get(mscCode1);           
            
            for (String author: authors.keySet()) {
                Map<String, Integer> classes = classesOfAuthor.get(author);
                                
                //System.out.println("LOOP2------------------------"+author+" ");
                for (String mscCode2: classes.keySet() ) {
                    if (classes.get(mscCode2) <= 0) { //nie bylo wystapienia takiego autora
                        continue;
                    }
                    
                    int prevValue = cooccurrence.get(mscCode1, mscCode2, 0);
                    
                    
                    //System.out.println("[buildCoocurrenceMatrix] "+mscCode1+" "+mscCode2+" "+author+" changing from:"+prevValue+" to:"+(prevValue+1));
                    cooccurrence.set(mscCode1, mscCode2, prevValue+1); //single author -> +1
                } //for every class of author
                //System.out.println("/LOOP2------------------------"+author+" ");
                
            } //for every author in class            
            
            //System.out.println("/LOOP1---------------------------------------------------"+mscCode1+" ");
        } //for every class
        
      
        //every class is counted two times so we need to divide
        log.info("[buildCoocurrenceMatrix] Recalculating.");
        for (int i=0; i<cc.size(); ++i) {
             for (int j=i+1; j<cc.size(); ++j) {
                 String mscCode1 = cc.get(i);
                 String mscCode2 = cc.get(j);
                 int prevValue = cooccurrence.get(mscCode1, mscCode2, 0);
                 if (prevValue > 0) {
                     cooccurrence.set(mscCode1, mscCode2, prevValue/2);
                 }
            }
        }

        
        return cooccurrence;
    }
    

    
}
