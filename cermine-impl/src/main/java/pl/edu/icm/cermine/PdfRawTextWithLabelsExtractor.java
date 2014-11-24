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

package pl.edu.icm.cermine;

import java.io.InputStream;
import org.jdom.Element;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Text extractor from PDF files. Extracted text includes 
 * all text strings found in the document in correct reading order
 * as well as labels for text fragments.
 *
 * @author Dominika Tkaczyk
 */
public class PdfRawTextWithLabelsExtractor {
    
    ComponentConfiguration conf;
    
    public PdfRawTextWithLabelsExtractor() throws AnalysisException {
        conf = new ComponentConfiguration();
    }
    
    /**
     * Extracts content of a PDF with labels.
     * 
     * @param stream input stream
     * @return pdf's content as plain text
     * @throws AnalysisException 
     */
    public Element extractRawText(InputStream stream) throws AnalysisException {
        return ExtractionUtils.extractRawTextWithLabels(conf, stream);
    }
    
    /**
     * Extracts content of a PDF with labels.
     * 
     * @param document document's structure
     * @return pdf's content as plain text
     * @throws AnalysisException 
     */
    public Element extractRawText(BxDocument document) throws AnalysisException {
        return ExtractionUtils.extractRawTextWithLabels(conf, document);
    }

    public ComponentConfiguration getConf() {
        return conf;
    }

    public void setConf(ComponentConfiguration conf) {
        this.conf = conf;
    }
    
}
