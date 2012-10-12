package pl.edu.icm.cermine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.bibref.BibReferenceExtractor;
import pl.edu.icm.cermine.bibref.BibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;


/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMReferencesExtractor implements DocumentReferencesExtractor<Element> {
    
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
    
    @Override
    public Element[] extractReferences(InputStream stream) throws AnalysisException {
        BibEntry[] entries = extractor.extractReferences(stream);
        List<Element> elements = new ArrayList<Element>(entries.length);
        for (BibEntry entry : entries) {
            elements.add(CitationUtils.bibEntryToNLM(entry));
        }
        return elements.toArray(new Element[entries.length]);
    }

    @Override
    public Element[] extractReferences(BxDocument document) throws AnalysisException {
        BibEntry[] entries = extractor.extractReferences(document);
        List<Element> elements = new ArrayList<Element>(entries.length);
        for (BibEntry entry : entries) {
            elements.add(CitationUtils.bibEntryToNLM(entry));
        }
        return elements.toArray(new Element[entries.length]);
    }

    public void setExtractor(DocumentReferencesExtractor<BibEntry> extractor) {
        this.extractor = extractor;
    }

}
