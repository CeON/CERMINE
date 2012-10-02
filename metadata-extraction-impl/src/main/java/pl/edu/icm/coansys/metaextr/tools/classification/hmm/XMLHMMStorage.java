package pl.edu.icm.coansys.metaextr.tools.classification.hmm;

import com.thoughtworks.xstream.XStream;
import java.io.*;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.model.*;

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
    public synchronized <S> void storeInitialProbability(String hmmId, HMMInitialProbability<S> probability)
            throws IOException {
        HMMProbabilityInfo<S> hmmProbability = getProbabilityInfo(hmmId);
        if (hmmProbability == null) {
            hmmProbability = new SimpleHMMProbabilityInfo<S>();
        }
        hmmProbability.setInitialProbability(probability);
        storeProbabilityInfo(hmmId, hmmProbability);
    }

    @Override
    public synchronized <S> void storeTransitionProbability(String hmmId, HMMTransitionProbability<S> probability)
            throws IOException {
        HMMProbabilityInfo<S> hmmProbability = getProbabilityInfo(hmmId);
        if (hmmProbability == null) {
            hmmProbability = new SimpleHMMProbabilityInfo<S>();
        }
        hmmProbability.setTransitionProbability(probability);
        storeProbabilityInfo(hmmId, hmmProbability);
    }

    @Override
    public synchronized <S> void storeEmissionProbability(String hmmId, HMMEmissionProbability<S> probability)
            throws IOException {
        HMMProbabilityInfo<S> hmmProbability = getProbabilityInfo(hmmId);
        if (hmmProbability == null) {
            hmmProbability = new SimpleHMMProbabilityInfo<S>();
        }
        hmmProbability.setEmissionProbability(probability);
        storeProbabilityInfo(hmmId, hmmProbability);
    }

    @Override
    public <S> HMMProbabilityInfo<S> getProbabilityInfo(String hmmId) throws IOException {
        String filePath = directory + File.separator + hmmId + File.separator + hmmProbabilitiesFile;
        InputStream is = null;

        try {
            is = new FileInputStream(filePath);
            XStream xstream = new XStream();
            Object object = xstream.fromXML(is);
            if (object instanceof HMMProbabilityInfo) {
                return (HMMProbabilityInfo) object;
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

    private <S,T> void storeProbabilityInfo(String hmmId, HMMProbabilityInfo<S> hmmProbability) throws IOException {
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
