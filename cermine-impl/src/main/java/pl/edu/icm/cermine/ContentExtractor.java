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

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.transformers.BibEntryToNLMElementConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * NLM-based content extractor from PDF files.
 *
 * @author Dominika Tkaczyk
 */
public class ContentExtractor {
    
    private PdfBxStructureExtractor structureExtractor;
    
    private PdfNLMMetadataExtractor metadataExtractor;
    
    private PdfBibEntryReferencesExtractor referencesExtractor;
    
    private PdfRawTextExtractor rawTextExtractor;
    
    private PdfNLMTextExtractor textExtractor;
           
    
    private InputStream pdfFile;
    private BxDocument bxDocument;
    private Element nlmMetadata;
    private List<BibEntry> bibEntryReferences;
    private List<Element> nlmReferences;
    private String rawFullText;
    private Element nlmFullText;
    private Element nlmContent;

    
    public ContentExtractor() throws AnalysisException {
        structureExtractor = new PdfBxStructureExtractor();
        metadataExtractor = new PdfNLMMetadataExtractor();
        referencesExtractor = new PdfBibEntryReferencesExtractor();
        rawTextExtractor = new PdfRawTextExtractor();
        textExtractor = new PdfNLMTextExtractor();
    }

    
    public void uploadPDF(InputStream pdfFile) throws IOException {
        this.reset();
        this.pdfFile = pdfFile;
    }
    
    public BxDocument getBxDocument() throws AnalysisException {
        if (pdfFile == null) {
            throw new AnalysisException("No PDF document uploaded!");
        }
        if (bxDocument == null) {
            bxDocument = structureExtractor.extractStructure(pdfFile);
        }
        return bxDocument;
    }
    
    public List<BibEntry> getBibEntryReferences() throws AnalysisException {
        if (bibEntryReferences == null) {
            getBxDocument();
            bibEntryReferences = Lists.newArrayList(referencesExtractor.extractReferences(bxDocument));
        }
        return bibEntryReferences;
    }
    
    public String getRawFullText() throws AnalysisException {
        if (rawFullText == null) {
            getBxDocument();
            rawFullText = rawTextExtractor.extractText(bxDocument);
        }
        return rawFullText;
    }
  
    public Element getNLMMetadata() throws AnalysisException {
        if (nlmMetadata == null) {
            getBxDocument();
            nlmMetadata = metadataExtractor.extractMetadata(bxDocument);
        }
        return nlmMetadata;
    }
    
    public List<Element> getNLMReferences() throws AnalysisException {
        if (nlmReferences == null) {
            getBibEntryReferences();
            nlmReferences = new ArrayList<Element>(bibEntryReferences.size());
            BibEntryToNLMElementConverter converter = new BibEntryToNLMElementConverter();
            for (BibEntry entry : bibEntryReferences) {
                try {
                    nlmReferences.add(converter.convert(entry));
                } catch (TransformationException ex) {
                    throw new AnalysisException(ex);
                }
            }
        }
        return nlmReferences;
    }
    
    public Element getNLMText() throws AnalysisException {
        if (nlmFullText == null) {
            getBxDocument();
            nlmFullText = textExtractor.extractText(bxDocument);
        }
        return nlmFullText;
    }
    
    public Element getNLMContent() throws AnalysisException {
        if (nlmContent == null) {
            getNLMMetadata();
            getNLMReferences();
            getNLMText();
            
            nlmContent = new Element("article");
            
            Element metadata = (Element) nlmMetadata.getChild("front").clone();
            nlmContent.addContent(metadata);
            
            nlmContent.addContent(nlmFullText);
            
            Element back = new Element("back");
            Element refList = new Element("ref-list");
            for (Element ref : nlmReferences) {
                Element r = new Element("ref");
                r.addContent(ref);
                refList.addContent(r);
            }
            back.addContent(refList);
            nlmContent.addContent(back);
        }
        return nlmContent;
    }
    
    public void reset() throws IOException {
        bxDocument = null;
        nlmMetadata = null;
        bibEntryReferences = null;
        nlmReferences = null;
        rawFullText = null;
        nlmFullText = null;
        nlmContent = null;
        if (pdfFile != null) {
            pdfFile.close();
        }
        pdfFile = null;
    }

    public void setMetadataExtractor(PdfNLMMetadataExtractor metadataExtractor) {
        this.metadataExtractor = metadataExtractor;
    }

    public void setRawTextExtractor(PdfRawTextExtractor rawTextExtractor) {
        this.rawTextExtractor = rawTextExtractor;
    }

    public void setReferencesExtractor(PdfBibEntryReferencesExtractor referencesExtractor) {
        this.referencesExtractor = referencesExtractor;
    }

    public void setStructureExtractor(PdfBxStructureExtractor structureExtractor) {
        this.structureExtractor = structureExtractor;
    }

    public void setTextExtractor(PdfNLMTextExtractor textExtractor) {
        this.textExtractor = textExtractor;
    }
    
}
