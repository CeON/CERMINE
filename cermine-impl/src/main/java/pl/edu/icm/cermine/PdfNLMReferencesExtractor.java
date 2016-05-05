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
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Parsed bibliograhic references extractor. Extracts references from a PDF file and stores them
 * in NLM format.
 *
 * @deprecated use {@link ContentExtractor} instead.
 * 
 * @author Dominika Tkaczyk
 */
@Deprecated
public class PdfNLMReferencesExtractor {
    
    private final ContentExtractor extractor;
    
    public PdfNLMReferencesExtractor() throws AnalysisException {
        extractor = new ContentExtractor();
    }
    
    /**
     * Extracts parsed bibliographic references from a PDF file and returns them as BibEntry objects.
     * 
     * @param stream input stream
     * @return parsed bibliographic references
     * @throws AnalysisException 
     */
    public BibEntry[] extractReferences(InputStream stream) throws AnalysisException {
        try {
            extractor.setPDF(stream);
            return extractor.getReferences().toArray(new BibEntry[]{});
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }

    /**
     * Extracts parsed bibliographic references from a PDF file and returns them as BibEntry objects.
     * 
     * @param document document's structure
     * @return parsed bibliographic references
     * @throws AnalysisException 
     */
    public BibEntry[] extractReferences(BxDocument document) throws AnalysisException {
        try {
            extractor.setBxDocument(document);
            return extractor.getReferences().toArray(new BibEntry[]{});
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }
    
    /**
     * Extracts parsed bibliographic references from a PDF file and stores them in NLM format.
     * 
     * @param stream input stream
     * @return parsed bibliographic references
     * @throws AnalysisException 
     */
    public Element[] extractReferencesAsNLM(InputStream stream) throws AnalysisException {
        try {
            extractor.setPDF(stream);
            return extractor.getNLMReferences().toArray(new Element[]{});
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }

    /**
     * Extracts parsed bibliographic references from a PDF file and stores them in NLM format.
     * 
     * @param document document's structure
     * @return parsed bibliographic references
     * @throws AnalysisException 
     */
    public Element[] extractReferencesAsNLM(BxDocument document) throws AnalysisException {
        try {
            extractor.setBxDocument(document);
            return extractor.getNLMReferences().toArray(new Element[]{});
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
