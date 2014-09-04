package pl.edu.icm.cermine.metadata.affiliations.tools;

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

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.parsing.tools.GrmmUtils;
import pl.edu.icm.cermine.parsing.tools.TokenClassifier;

public class AffiliationCRFTokenClassifier extends TokenClassifier<AffiliationLabel, AffiliationToken> {

	private ACRF model;
    private static final String DEFAULT_MODEL_FILE = "acrf8000.ser.gz";
    private static final int DEFAULT_NEIGHBOR_INFLUENCE_THRESHOLD = 1;
	
	public AffiliationCRFTokenClassifier(InputStream modelInputStream) throws AnalysisException {
        System.setProperty("java.util.logging.config.file",
            "edu/umass/cs/mallet/base/util/resources/logging.properties");
        if (modelInputStream == null) {
        	throw new AnalysisException("Cannot set model, input stream is null!");
        }
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
	
	public AffiliationCRFTokenClassifier() throws AnalysisException {
		this(AffiliationCRFTokenClassifier.class.getResourceAsStream(DEFAULT_MODEL_FILE));
	}

	private LineGroupIterator getLineIterator(String data) {
		return new LineGroupIterator(new StringReader(data), Pattern.compile ("\\s*"), true);
	}
	
	@Override
	public void classify(List<AffiliationToken> tokens) throws AnalysisException {
        String data = GrmmUtils.toGrmmInput(tokens, DEFAULT_NEIGHBOR_INFLUENCE_THRESHOLD);
        
        Pipe pipe = model.getInputPipe();
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(getLineIterator(data)); 
        LabelsSequence labelSequence = (LabelsSequence)model.getBestLabels(instanceList).get(0);
           
        for (int i = 0; i < labelSequence.size(); i++) {
            tokens.get(i).setLabel(AffiliationLabel.createLabel(labelSequence.get(i).toString()));
        }
		
	}
}
