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
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Text extractor from PDF files. Extracted text includes 
 * all text string found in the document in correct reading order.
 *
 * @author Paweł Szostek
 * @author Dominika Tkaczyk
 */
public class PdfNLMTextExtractor {
  
    private ComponentConfiguration conf;
    
    public PdfNLMTextExtractor() throws AnalysisException {
        conf = new ComponentConfiguration();
    }

    /**
     * Extracts full text from input stream.
     * 
     * @param stream PDF stream
     * @return document's full text in NLM format
     * @throws AnalysisException 
     */
    public Element extractTextAsNLM(InputStream stream) throws AnalysisException {
        return ExtractionUtils.extractTextAsNLM(conf, stream);
    }

    /**
     * Extracts full text from document's box structure.
     * 
     * @param document box structure
     * @return document's full text in NLM format
     * @throws AnalysisException 
     */
    public Element extractTextAsNLM(BxDocument document) throws AnalysisException {
        return ExtractionUtils.extractTextAsNLM(conf, document);
    }
    
    /**
     * Extracts full text from input stream.
     * 
     * @param stream PDF stream
     * @return document's full text
     * @throws AnalysisException 
     */
    public ContentStructure extractText(InputStream stream) throws AnalysisException {
        return ExtractionUtils.extractText(conf, stream);
    }

    /**
     * Extracts full text from document's box structure.
     * 
     * @param document box structure
     * @return document's full text
     * @throws AnalysisException 
     */
    public ContentStructure extractText(BxDocument document) throws AnalysisException {
        return ExtractionUtils.extractText(conf, document);
    }

    public ComponentConfiguration getConf() {
        return conf;
    }

    public void setConf(ComponentConfiguration conf) {
        this.conf = conf;
    }
    
}
