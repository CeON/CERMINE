package pl.edu.icm.yadda.analysis.bibref;

import edu.umass.cs.mallet.base.pipe.Pipe;
import edu.umass.cs.mallet.base.pipe.iterator.LineGroupIterator;
import edu.umass.cs.mallet.base.types.InstanceList;
import edu.umass.cs.mallet.base.types.LabelsSequence;
import edu.umass.cs.mallet.grmm.learning.ACRF;
import java.io.*;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang.StringUtils;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.yadda.analysis.bibref.parsing.tools.CitationUtils;

/**
 *
 * @author Dominika Tkaczyk
 */
public class CRFBibReferenceParser implements BibReferenceParser<BibEntry> {
    
    private String modelFile = "/tmp/acrf.ser.gz";

    @Override
	public BibEntry parseBibReference(String text) throws AnalysisException {
        try {
            Citation citation = CitationUtils.stringToCitation(text);
            String data = StringUtils.join(CitationUtils.citationToMalletInputFormat(citation), "\n");

            ACRF model = readModel();
            Pipe pipe = model.getInputPipe();
            InstanceList instanceList = new InstanceList(pipe);
            instanceList.add(new LineGroupIterator(new StringReader(data), Pattern.compile ("\\s*"), true)); 
            LabelsSequence labelSequence = (LabelsSequence)model.getBestLabels(instanceList).get(0);
            
            for (int i = 0; i < labelSequence.size(); i++) {
                citation.getTokens().get(i).setLabel(CitationTokenLabel.valueOf(labelSequence.get(i).toString()));
            }
            
            return CitationUtils.citationToBibref(citation);
		
        } catch (ClassNotFoundException ex) {
            throw new AnalysisException("Cannot parse reference", ex);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot parse reference", ex);
        }
	}
    
    private ACRF readModel() throws IOException, ClassNotFoundException {
        InputStream is = new FileInputStream(new File(modelFile));
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(is)));
        return (ACRF)(ois.readObject());
    }

    public void setModelFile(String modelFile) {
        this.modelFile = modelFile;
    }
    
}
