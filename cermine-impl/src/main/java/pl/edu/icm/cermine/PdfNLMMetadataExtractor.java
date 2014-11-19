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
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;


/**
 * NLM-based metadata extractor from PDF files.
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMMetadataExtractor {

    private ComponentConfiguration conf;
    
    public PdfNLMMetadataExtractor() throws AnalysisException {
        conf = new ComponentConfiguration();
    }
    
    /**
     * Extracts metadata from input stream.
     *
     * @param stream PDF stream
     * @return document's metadata
     * @throws AnalysisException 
     */
    public DocumentMetadata extractMetadata(InputStream stream) throws AnalysisException {
        return ExtractionUtils.extractMetadata(conf, stream);
    }
    
    /**
     * Extracts NLM metadata from input stream.
     * 
     * @param stream PDF stream
     * @return document's metadata in NLM format
     * @throws AnalysisException 
     */
    public Element extractMetadataAsNLM(InputStream stream) throws AnalysisException {
        return ExtractionUtils.extractMetadataAsNLM(conf, stream);
    }
    
    /**
     * Extracts metadata from document's structure.
     * 
     * @param document box structure
     * @return document's metadata
     * @throws AnalysisException 
     */
    public DocumentMetadata extractMetadata(BxDocument document) throws AnalysisException {
        return ExtractionUtils.extractMetadata(conf, document);
    }
    
    /**
     * Extracts NLM metadata from document's structure.
     * 
     * @param document box structure
     * @return document's metadata in NLM format
     * @throws AnalysisException 
     */
    public Element extractMetadataAsNLM(BxDocument document) throws AnalysisException {
        return ExtractionUtils.extractMetadataAsNLM(conf, document);
    }

    public ComponentConfiguration getConf() {
        return conf;
    }

    public void setConf(ComponentConfiguration conf) {
        this.conf = conf;
    }
    
}