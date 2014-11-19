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
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Parsed bibliograhic references extractor. Extracts references from a PDF file and stores them
 * in NLM format.
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMReferencesExtractor {
    
    private ComponentConfiguration conf;
    
    public PdfNLMReferencesExtractor() throws AnalysisException {
        conf = new ComponentConfiguration();
    }
    
    /**
     * Extracts parsed bibliographic references from a PDF file and returns them as BibEntry objects.
     * 
     * @param stream input stream
     * @return parsed bibliographic references
     * @throws AnalysisException 
     */
    public BibEntry[] extractReferences(InputStream stream) throws AnalysisException {
        return ExtractionUtils.extractReferences(conf, stream);
    }

    /**
     * Extracts parsed bibliographic references from a PDF file and returns them as BibEntry objects.
     * 
     * @param document document's structure
     * @return parsed bibliographic references
     * @throws AnalysisException 
     */
    public BibEntry[] extractReferences(BxDocument document) throws AnalysisException {
        return ExtractionUtils.extractReferences(conf, document);
    }
    
    /**
     * Extracts parsed bibliographic references from a PDF file and stores them in NLM format.
     * 
     * @param stream input stream
     * @return parsed bibliographic references
     * @throws AnalysisException 
     */
    public Element[] extractReferencesAsNLM(InputStream stream) throws AnalysisException {
        return ExtractionUtils.extractReferencesAsNLM(conf, stream);
    }

    /**
     * Extracts parsed bibliographic references from a PDF file and stores them in NLM format.
     * 
     * @param document document's structure
     * @return parsed bibliographic references
     * @throws AnalysisException 
     */
    public Element[] extractReferencesAsNLM(BxDocument document) throws AnalysisException {
        return ExtractionUtils.extractReferencesAsNLM(conf, document);
    }

    public ComponentConfiguration getConf() {
        return conf;
    }

    public void setConf(ComponentConfiguration conf) {
        this.conf = conf;
    }
    
}
