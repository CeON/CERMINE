package pl.edu.icm.yadda.analysis.mscsimilarity;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.schlichtherle.io.FileOutputStream;

import pl.edu.icm.yadda.analysis.datastructures.SymmetricTreeMapMatrix;
import pl.edu.icm.yadda.analysis.datastructures.TreeMapMatrix;
import pl.edu.icm.yadda.bwmeta.model.YCategoryRef;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IInitializableFinalizableNode;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * Generates statistics of Msc classes co-occurrence (number of co-occurences)
 * basing on incrementally coming yelements.
 * 
 * @author tkusm
 * 
 */
public class MscClassesCooccurrenceStatsWritingNode implements IWriterNode<List<YElement>>,
        IInitializableFinalizableNode {

    /**
     * Logger.
     */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Symmetric matrix of type MscClassCode x MscClassCode-> number of
     * co-occurrences.
     */
    protected SymmetricTreeMapMatrix<String, Integer> cooccurrence = new SymmetricTreeMapMatrix<String, Integer>();

    /**
     * What's the name of the parameter that says where to store output matrix.
     */
    public static final String AUX_PARAM_OUTPUT_PATH = "copath";
    /**
     * What's the name of the parameter that says what value should be put into empty places in output matrix.
     */
    public static final String AUX_PARAM_DEFAULT_VALUE = "defval";

    TreeMapMatrix<String, String, Integer> getCooccurrence() {
        return cooccurrence;
    }

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
        List<YCategoryRef> mscRefs = extractMscRefs(yelement.getCategoryRefs());
        if (mscRefs.size() == 0) {
            return;
        }
        log.info("analyzing next yelement = [{}]", yelement);

        for (int i = 0; i < mscRefs.size(); ++i) {
            for (int j = i; j < mscRefs.size(); ++j) {
                String msc1 = mscRefs.get(i).getCode();
                String msc2 = mscRefs.get(j).getCode();

                Integer previousValue = cooccurrence.get(msc1, msc2, 0);
                // System.out.println("Incrementing ["+msc1+", "+msc2+"] from "+previousValue+" to "+(previousValue+1));
                cooccurrence.set(msc1, msc2, (previousValue + 1));
            }
        }

        /*
         * for (YCategoryRef msc1 : mscRefs) { for (YCategoryRef msc2 : mscRefs)
         * { Integer previousValue = cooccurrence.get(msc1.getCode(),
         * msc2.getCode(), 0); cooccurrence.set(msc1.getCode(), msc2.getCode(),
         * previousValue + 1); } }
         */

    }

    /**
     * @param refsa
     *            all the category references.
     * @return category references of Msc classification.
     */
    private List<YCategoryRef> extractMscRefs(List<YCategoryRef> refs) {
        List<YCategoryRef> mscRefs = new ArrayList<YCategoryRef>();

        for (YCategoryRef ref : refs) {
            if (ref.getClassification().equals(YConstants.EXT_CLASSIFICATION_MSC)) {
                mscRefs.add(ref);
            }
        }
        return mscRefs;
    }

    @Override
    public void finalize(ProcessContext ctx) throws Exception {
        try {
            String path = getOutputPath(ctx);
            log.info("Writing matrix to file [{}].", path);
            FileOutputStream fos = new FileOutputStream(path);
            TreeMapMatrix.storePlainText(cooccurrence, fos, TreeMapMatrix.SUGGESTED_SEPARATOR, getMatrixDefaultValue(ctx));
        } catch (Exception e) {
            log.error("MscClassesCoocurrence Exception: " + e.getMessage());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(stream));
            log.error("MscClassesCoocurrence Exception StackTrace: " + stream.toString());
        } catch (Throwable t) {
            log.error("MscClassesCoocurrence Throwable: " + t.getMessage());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(stream));
            log.error("MscClassesCoocurrence Throwable StackTrace: " + stream.toString());
        }
    }

    private String getOutputPath(ProcessContext ctx) {
        String path = (String) ctx.getAuxParam(AUX_PARAM_OUTPUT_PATH);
        if (path == null || path.length() <= 0) {
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

}
