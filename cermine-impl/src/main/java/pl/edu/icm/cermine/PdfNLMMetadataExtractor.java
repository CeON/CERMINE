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

import java.io.IOException;
import java.io.InputStream;
import org.jdom.Element;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;


/**
 * NLM-based metadata extractor from PDF files.
 *
 * @deprecated use {@link ContentExtractor} instead.
 * 
 * @author Dominika Tkaczyk
 */
@Deprecated
public class PdfNLMMetadataExtractor {

    private final ContentExtractor extractor;
    
    public PdfNLMMetadataExtractor() throws AnalysisException {
        extractor = new ContentExtractor();
    }
    
    /**
     * Extracts metadata from input stream.
     *
     * @param stream PDF stream
     * @return document's metadata
     * @throws AnalysisException 
     */
    public DocumentMetadata extractMetadata(InputStream stream) throws AnalysisException {
        try {
            extractor.setPDF(stream);
            return extractor.getMetadata();
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }
    
    /**
     * Extracts NLM metadata from input stream.
     * 
     * @param stream PDF stream
     * @return document's metadata in NLM format
     * @throws AnalysisException 
     */
    public Element extractMetadataAsNLM(InputStream stream) throws AnalysisException {
        try {
            extractor.setPDF(stream);
            return extractor.getNLMMetadata();
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }
    
    /**
     * Extracts metadata from document's structure.
     * 
     * @param document box structure
     * @return document's metadata
     * @throws AnalysisException 
     */
    public DocumentMetadata extractMetadata(BxDocument document) throws AnalysisException {
        try {
            extractor.setBxDocument(document);
            return extractor.getMetadata();
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }
    
    /**
     * Extracts NLM metadata from document's structure.
     * 
     * @param document box structure
     * @return document's metadata in NLM format
     * @throws AnalysisException 
     */
    public Element extractMetadataAsNLM(BxDocument document) throws AnalysisException {
        try {
            extractor.setBxDocument(document);
            return extractor.getNLMMetadata();
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }

    public ComponentConfiguration getConf() {
        return extractor.getConf();
    }

    public void setConf(ComponentConfiguration conf) {
        extractor.setConf(conf);
    }
    
}