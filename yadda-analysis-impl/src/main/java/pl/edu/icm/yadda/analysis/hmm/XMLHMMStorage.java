package pl.edu.icm.yadda.analysis.hmm;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import pl.edu.icm.yadda.analysis.hmm.probability.HMMEmissionProbability;
import pl.edu.icm.yadda.analysis.hmm.probability.HMMInitialProbability;
import pl.edu.icm.yadda.analysis.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.hmm.probability.SimpleHMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.hmm.probability.HMMTransitionProbability;

/**
 * Hidden Markov Model storage implementation that stores HMM's probability
 * information in XML files.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class XMLHMMStorage implements HMMStorage {

    private String directory = "/tmp/hmm/";
    private static final String hmmProbabilitiesFile = "hmm-probabilities.xml";

    @Override
    public synchronized <S,T> void storeInitialProbability(String hmmId, HMMInitialProbability<S> probability)
            throws IOException {
        HMMProbabilityInfo<S,T> hmmProbability = getProbabilityInfo(hmmId);
        if (hmmProbability == null) {
            hmmProbability = new SimpleHMMProbabilityInfo<S,T>();
        }
        hmmProbability.setInitialProbability(probability);
        storeProbabilityInfo(hmmId, hmmProbability);
    }

    @Override
    public synchronized <S,T> void storeTransitionProbability(String hmmId, HMMTransitionProbability<S> probability)
            throws IOException {
        HMMProbabilityInfo<S,T> hmmProbability = getProbabilityInfo(hmmId);
        if (hmmProbability == null) {
            hmmProbability = new SimpleHMMProbabilityInfo<S,T>();
        }
        hmmProbability.setTransitionProbability(probability);
        storeProbabilityInfo(hmmId, hmmProbability);
    }

    @Override
    public synchronized <S,T> void storeEmissionProbability(String hmmId, HMMEmissionProbability<S,T> probability)
            throws IOException {
        HMMProbabilityInfo<S,T> hmmProbability = getProbabilityInfo(hmmId);
        if (hmmProbability == null) {
            hmmProbability = new SimpleHMMProbabilityInfo<S,T>();
        }
        hmmProbability.setEmissionProbability(probability);
        storeProbabilityInfo(hmmId, hmmProbability);
    }

    @Override
    public <S,T> HMMProbabilityInfo<S,T> getProbabilityInfo(String hmmId) throws IOException {
        String filePath = directory + File.separator + hmmId + File.separator + hmmProbabilitiesFile;
        InputStream is = null;

        try {
            is = new FileInputStream(filePath);
            XStream xstream = new XStream();
            Object object = xstream.fromXML(is);
            if (object instanceof HMMProbabilityInfo) {
                return (HMMProbabilityInfo<S,T>) object;
            }
            return null;
        } catch (FileNotFoundException ex) {
            return null;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private <S,T> void storeProbabilityInfo(String hmmId, HMMProbabilityInfo<S,T> hmmProbability) throws IOException {
        String filePath = directory + File.separator + hmmId + File.separator + hmmProbabilitiesFile;
        File hmmFile = new File(filePath);
        if (!hmmFile.exists()) {
            hmmFile.getParentFile().mkdirs();
            hmmFile.createNewFile();
        }
        Writer w = new FileWriter(directory + hmmId + "/" + hmmProbabilitiesFile);
        XStream xstream = new XStream();
        xstream.toXML(hmmProbability, w);
        w.close();
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
