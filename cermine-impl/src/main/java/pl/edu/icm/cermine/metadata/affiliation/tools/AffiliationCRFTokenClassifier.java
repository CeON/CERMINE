/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
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
package pl.edu.icm.cermine.metadata.affiliation.tools;

import edu.umass.cs.mallet.base.pipe.Pipe;
import edu.umass.cs.mallet.base.pipe.iterator.LineGroupIterator;
import edu.umass.cs.mallet.base.types.InstanceList;
import edu.umass.cs.mallet.base.types.LabelsSequence;
import edu.umass.cs.mallet.grmm.learning.ACRF;
import java.io.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.tools.GrmmUtils;
import pl.edu.icm.cermine.parsing.tools.TokenClassifier;

/**
 * Token classifier suitable for processing affiliations.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @author Bartosz Tarnawski
 */
public class AffiliationCRFTokenClassifier implements TokenClassifier<Token<AffiliationLabel>> {

    private ACRF model;
    private static final int DEFAULT_NEIGHBOR_INFLUENCE_THRESHOLD = 1;
    private static final String DEFAULT_MODEL_FILE
            = "/pl/edu/icm/cermine/metadata/affiliation/acrf-affiliations-pubmed.ser.gz";

    /**
     * @param modelInputStream the stream representing the ACRF model to be used
     * @throws AnalysisException if the model cannot be loaded
     */
    public AffiliationCRFTokenClassifier(InputStream modelInputStream) throws AnalysisException {
        // prevents MALLET from printing info messages
        System.setProperty("java.util.logging.config.file",
                "edu/umass/cs/mallet/base/util/resources/logging.properties");
        
        if (modelInputStream == null) {
            throw new AnalysisException("Cannot set model, input stream is null!");
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(
                    modelInputStream)));
            model = (ACRF) (ois.readObject());
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

    /**
     * Uses the default ACRF model.
     *
     * @throws AnalysisException AnalysisException
     */
    public AffiliationCRFTokenClassifier() throws AnalysisException {
        this(AffiliationCRFTokenClassifier.class.getResourceAsStream(DEFAULT_MODEL_FILE));
    }

    private LineGroupIterator getLineIterator(String data) {
        return new LineGroupIterator(new StringReader(data), Pattern.compile("\\s*"), true);
    }

    /**
     * When comma is the last token in a tagged part, its label is changed to
     * 'TEXT'.
     *
     * @param tokens
     */
    private void enhanceLabels(List<Token<AffiliationLabel>> tokens) {
        Token<AffiliationLabel> lastToken = null;
        for (Token<AffiliationLabel> token : tokens) {
            if (lastToken != null && lastToken.getLabel() != token.getLabel()
                    && lastToken.getText().equals(",")) {
                lastToken.setLabel(AffiliationLabel.TEXT);
            }
            lastToken = token;
        }
    }

    @Override
    public void classify(List<Token<AffiliationLabel>> tokens) throws AnalysisException {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }
        for (Token<AffiliationLabel> token : tokens) {
            if (token.getLabel() == null) {
                token.setLabel(AffiliationLabel.TEXT);
            }
        }
        String data = GrmmUtils.toGrmmInput(tokens, DEFAULT_NEIGHBOR_INFLUENCE_THRESHOLD);

        Pipe pipe = model.getInputPipe();
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(getLineIterator(data));
        LabelsSequence labelsSequence = null;

        try {
            labelsSequence = (LabelsSequence) model.getBestLabels(instanceList).get(0);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new AnalysisException("ACRF model can't recognize some of the labels.");
        }

        for (int i = 0; i < labelsSequence.size(); i++) {
            tokens.get(i).setLabel(AffiliationLabel.valueOf(labelsSequence.get(i).toString()));
        }

        enhanceLabels(tokens);
    }
}
