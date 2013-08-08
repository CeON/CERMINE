/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.tools.classification.hmm;

import com.thoughtworks.xstream.XStream;
import java.io.*;
import pl.edu.icm.cermine.tools.classification.hmm.model.*;

/**
 * Hidden Markov Model storage implementation that stores HMM's probability
 * information in XML files.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class XMLHMMStorage implements HMMStorage {

    private String directory = "/tmp/hmm/";
    private static final String HMM_PROB_FILE = "hmm-probabilities.xml";

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
        String filePath = directory + File.separator + hmmId + File.separator + HMM_PROB_FILE;
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
        String filePath = directory + File.separator + hmmId + File.separator + HMM_PROB_FILE;
        File hmmFile = new File(filePath);
        if (!hmmFile.exists()) {
            if (!hmmFile.getParentFile().mkdirs()) {
                throw new IOException("Cannot create directories!");
            }
            if (!hmmFile.createNewFile()) {
                throw new IOException("Cannot create file!");
            }
        }
        Writer w = new FileWriter(directory + hmmId + "/" + HMM_PROB_FILE);
        XStream xstream = new XStream();
        xstream.toXML(hmmProbability, w);
        w.close();
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
