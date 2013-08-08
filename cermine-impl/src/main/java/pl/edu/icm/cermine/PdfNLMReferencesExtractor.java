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
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.bibref.BibReferenceExtractor;
import pl.edu.icm.cermine.bibref.BibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.transformers.BibEntryToNLMElementConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Parsed bibliograhic references extractor. Extracts references from a PDF file and stores them
 * in NLM format.
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMReferencesExtractor implements DocumentReferencesExtractor<Element> {
    
    /** BibEntry-based references extractor */
    private DocumentReferencesExtractor<BibEntry> extractor;
    
    public PdfNLMReferencesExtractor() throws AnalysisException {
        extractor = new PdfBibEntryReferencesExtractor();
    }
    
    public PdfNLMReferencesExtractor(InputStream model) throws AnalysisException {
        extractor = new PdfBibEntryReferencesExtractor(model);
    }
    
    public PdfNLMReferencesExtractor(DocumentStructureExtractor strExtractor, BibReferenceExtractor extractor, BibReferenceParser<BibEntry> parser) {
        this.extractor = new PdfBibEntryReferencesExtractor(strExtractor, extractor, parser);
    }
    
    /**
     * Extracts parsed bibliographic references from a PDF file and stores them in NLM format.
     * 
     * @param stream
     * @return parsed bibliographic references
     * @throws AnalysisException 
     */
    @Override
    public Element[] extractReferences(InputStream stream) throws AnalysisException {
        return extractReferences(extractor.extractReferences(stream));
    }

    /**
     * Extracts parsed bibliographic references from a PDF file and stores them in NLM format.
     * 
     * @param document
     * @return parsed bibliographic references
     * @throws AnalysisException 
     */
    @Override
    public Element[] extractReferences(BxDocument document) throws AnalysisException {
        return extractReferences(extractor.extractReferences(document));
    }
    
    private Element[] extractReferences(BibEntry[] entries) throws AnalysisException {
        List<Element> elements = new ArrayList<Element>(entries.length);
        BibEntryToNLMElementConverter converter = new BibEntryToNLMElementConverter();
        for (BibEntry entry : entries) {
            try {
                elements.add(converter.convert(entry));
            } catch (TransformationException ex) {
                throw new AnalysisException(ex);
            }
        }
        return elements.toArray(new Element[entries.length]);
    }

    public void setExtractor(DocumentReferencesExtractor<BibEntry> extractor) {
        this.extractor = extractor;
    }

}
