package pl.edu.icm.cermine.affparse.tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import edu.umass.cs.mallet.base.pipe.Pipe;
import edu.umass.cs.mallet.base.pipe.iterator.LineGroupIterator;
import edu.umass.cs.mallet.base.types.InstanceList;
import edu.umass.cs.mallet.base.types.LabelsSequence;
import edu.umass.cs.mallet.grmm.learning.ACRF;

import pl.edu.icm.cermine.affparse.model.AffiliationLabel;
import pl.edu.icm.cermine.affparse.model.AffiliationToken;
import pl.edu.icm.cermine.exception.AnalysisException;

public class AffiliationCRFTokenClassifier extends TokenClassifier<AffiliationLabel, AffiliationToken> {

	private ACRF model;
	
	public AffiliationCRFTokenClassifier(InputStream modelInputStream) throws AnalysisException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(modelInputStream)));
            model = (ACRF)(ois.readObject());
        } catch (IOException ex) {
            throw new AnalysisException("Cannot set model!", ex);
        } catch (ClassNotFoundException ex) {
            throw new AnalysisException("Cannot set model!", ex);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                throw new AnalysisException("Cannot set model!", ex);
            }
        }
	}

	private LineGroupIterator getLineIterator(String data) {
		return new LineGroupIterator(new StringReader(data), Pattern.compile ("\\s*"), true);
	}
	
	@Override
	public void classify(List<AffiliationToken> tokens) throws AnalysisException {
		StringBuilder grmmInputBuilder = new StringBuilder();
		for (AffiliationToken token : tokens) {
			grmmInputBuilder.append(GrmmUtils.toGrmmInput(token));
			grmmInputBuilder.append("\n");
		}
		
        String data = grmmInputBuilder.toString();
        
        Pipe pipe = model.getInputPipe();
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(getLineIterator(data)); 
        LabelsSequence labelSequence = (LabelsSequence)model.getBestLabels(instanceList).get(0);
           
        for (int i = 0; i < labelSequence.size(); i++) {
            tokens.get(i).setLabel(AffiliationLabel.createLabel(labelSequence.get(i).toString()));
        }
		
	}
	
    public static AffiliationCRFTokenClassifier getInstance() throws AnalysisException {
        return new AffiliationCRFTokenClassifier(
        		AffiliationCRFTokenClassifier.class.getResourceAsStream(DEFAULT_MODEL_FILE));
    }

    private static final String DEFAULT_MODEL_FILE = "acrf.ser.gz";
}
