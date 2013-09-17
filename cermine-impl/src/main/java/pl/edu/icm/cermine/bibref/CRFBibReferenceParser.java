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

package pl.edu.icm.cermine.bibref;

import edu.umass.cs.mallet.base.pipe.Pipe;
import edu.umass.cs.mallet.base.pipe.iterator.LineGroupIterator;
import edu.umass.cs.mallet.base.types.InstanceList;
import edu.umass.cs.mallet.base.types.LabelsSequence;
import edu.umass.cs.mallet.grmm.learning.ACRF;
import java.io.*;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang.StringUtils;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * CRF-based bibiliographic reference parser.
 * 
 * @author Dominika Tkaczyk
 */
public class CRFBibReferenceParser implements BibReferenceParser<BibEntry> {
    
    private static final int MAX_REFERENCE_LENGTH = 2000;
    
    private ACRF model;

    public CRFBibReferenceParser(String modelFile) throws AnalysisException {
        InputStream is;
        ObjectInputStream ois = null;
        try {
            is = new FileInputStream(new File(modelFile));
            ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(is)));
            model = (ACRF)(ois.readObject());
        } catch (ClassNotFoundException ex) {
            throw new AnalysisException("Cannot set model!", ex);
        } catch (IOException ex) {
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
    
    public CRFBibReferenceParser(InputStream modelInputStream) throws AnalysisException {
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

    @Override
	public BibEntry parseBibReference(String text) throws AnalysisException {
        if (text.length() > MAX_REFERENCE_LENGTH) {
            return new BibEntry().setText(text);
        }
        
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
  
}
