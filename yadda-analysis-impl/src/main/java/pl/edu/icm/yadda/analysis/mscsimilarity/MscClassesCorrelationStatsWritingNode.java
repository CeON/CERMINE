package pl.edu.icm.yadda.analysis.mscsimilarity;

import java.io.*;
import java.util.*;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import pl.edu.icm.yadda.analysis.datastructures.*;
import pl.edu.icm.yadda.bwmeta.model.YCategoryRef;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IInitializableFinalizableNode;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * Calculates class-vs-class matrix of Pearson correlation coefficients. 
 * 
 * 
 * @author tkusm
 *
 */
public class MscClassesCorrelationStatsWritingNode implements IWriterNode<List<YElement>>,
IInitializableFinalizableNode {

    /**
     * Logger.
     */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @key category name
     * @value list of documents' indexes for which category occurred
     */
    protected Map<String, List<Integer>> occurrences = new HashMap<String, List<Integer>>();;

    /**
     * Counts processed yelements (documents).
     */
    protected int documentCounter = 0;
    
    /**
     * What's the name of the parameter that says where to store output matrix.
     */
    public static final String AUX_PARAM_OUTPUT_PATH = "corpath";
    /**
     * What's the name of the parameter that says what value should be put into empty places in output matrix.
     */
    public static final String AUX_PARAM_DEFAULT_VALUE = "cordefval";
    

  

    @Override
    public void initialize(ProcessContext ctx) throws Exception {
    	documentCounter = 0;
    	occurrences = new HashMap<String, List<Integer>>();
    }

    @Override
    public void store(List<YElement> yelements, ProcessContext ctx) throws Exception {
        synchronized (this) {
            log.info("yelements.size() = {}", yelements.size());
            for (YElement yelement : yelements) {
                log.info("updating with next yelement = [{}]", yelement);
                updateStats(yelement);
                documentCounter++;	//count processed documents
            } // foreach in yelements
        }
    }

    private void updateStats(YElement yelement) {
        List<String> mscRefsList = extractMscRefs(yelement.getCategoryRefs());
        if (mscRefsList.size() == 0) {
            return;
        }
                
        for (String msc: mscRefsList) {
        	if (!occurrences.containsKey(msc)) {
        		occurrences.put(msc, new ArrayList<Integer>());
        	}
        	
        	occurrences.get(msc).add(documentCounter); //Remember that category occurred
        }
    }

    /**
     * @param refs all the category names.
     * @return categories of Msc classification.
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

    @Override
    public void finalize(ProcessContext ctx) throws Exception {    	
        try {
            String path = getOutputPath(ctx);
            log.info("Writing matrix to file [{}].", path);
            FileOutputStream fos = new FileOutputStream(path);
            PrintStream osw = new PrintStream(fos);
            
            printCorrelationMatrix(osw);
            
            fos.close();
        } catch (Exception e) {
            log.error("MscClassesCorrelationStatsWritingNode Exception: " + e.getMessage());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(stream));
            log.error("MscClassesCorrelationStatsWritingNode Exception StackTrace: " + stream.toString());
        } catch (Throwable t) {
            log.error("MscClassesCorrelationStatsWritingNode Throwable: " + t.getMessage());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(stream));
            log.error("MscClassesCorrelationStatsWritingNode Throwable StackTrace: " + stream.toString());
        } 
    }

	public void printCorrelationMatrix(PrintStream osw) {
		BinarySignalCorrelactionCalculator.printCorrelationMatrix(osw, occurrences, documentCounter);
	}
	
	public SymmetricTreeMapMatrix<String, Double> getCorrelationMatrix() {
		return BinarySignalCorrelactionCalculator.generateCorrelationMatrix(occurrences, documentCounter);
	}



    private String getOutputPath(ProcessContext ctx) {
        String path = (String) ctx.getAuxParam(AUX_PARAM_OUTPUT_PATH);
        if (path == null || path.length() <= 0) {
            log.error("Process parameter [{}] must be set!", AUX_PARAM_OUTPUT_PATH);
        }
        return path;
    }


}
