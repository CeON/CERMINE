package pl.edu.icm.coansys.metaextr.bibref;

import edu.umass.cs.mallet.base.pipe.Pipe;
import edu.umass.cs.mallet.base.pipe.iterator.LineGroupIterator;
import edu.umass.cs.mallet.base.types.InstanceList;
import edu.umass.cs.mallet.base.types.LabelsSequence;
import edu.umass.cs.mallet.grmm.learning.ACRF;
import java.io.*;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang.StringUtils;
import pl.edu.icm.coansys.metaextr.bibref.model.BibEntry;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.coansys.metaextr.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class CRFBibReferenceParser implements BibReferenceParser<BibEntry> {
    
    private ACRF model;

    @Override
	public BibEntry parseBibReference(String text) throws AnalysisException {
        if (model == null) {
            throw new AnalysisException("Model object is not set!");
        }
        
        Citation citation = CitationUtils.stringToCitation(text);
        String data = StringUtils.join(CitationUtils.citationToMalletInputFormat(citation), "\n");

        Pipe pipe = model.getInputPipe();
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(new LineGroupIterator(new StringReader(data), Pattern.compile ("\\s*"), true)); 
        LabelsSequence labelSequence = (LabelsSequence)model.getBestLabels(instanceList).get(0);
           
        for (int i = 0; i < labelSequence.size(); i++) {
            citation.getTokens().get(i).setLabel(CitationTokenLabel.valueOf(labelSequence.get(i).toString()));
        }
            
        return CitationUtils.citationToBibref(citation);
    }
    
    public void setModel(String modelFile) throws AnalysisException {
        InputStream is;
        try {
            is = new FileInputStream(new File(modelFile));
        } catch (FileNotFoundException ex) {
            throw new AnalysisException("Cannot set model!", ex);
        }
        setModel(is);
    }
    
    public void setModel(InputStream modelInputStream) throws AnalysisException {
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(modelInputStream)));
            model = (ACRF)(ois.readObject());
        } catch (IOException ex) {
            throw new AnalysisException("Cannot set model!", ex);
        } catch (ClassNotFoundException ex) {
            throw new AnalysisException("Cannot set model!", ex);
        }
    }
    
}
