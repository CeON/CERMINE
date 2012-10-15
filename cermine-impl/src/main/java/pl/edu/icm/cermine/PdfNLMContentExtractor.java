package pl.edu.icm.cermine;

import java.io.InputStream;
import org.jdom.Element;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMContentExtractor implements DocumentContentExtractor<Element> {
    
    private DocumentStructureExtractor structureExtractor;
    private DocumentMetadataExtractor<Element> metadataExtractor;
    private DocumentReferencesExtractor<Element> referencesExtractor;

    public PdfNLMContentExtractor() throws AnalysisException {
        structureExtractor = new PdfBxStructureExtractor();
        metadataExtractor = new PdfNLMMetadataExtractor();
        referencesExtractor = new PdfNLMReferencesExtractor();
    }
    
    @Override
    public Element extractContent(InputStream stream) throws AnalysisException {
        BxDocument document = structureExtractor.extractStructure(stream);
        return extractContent(document);
    }

    @Override
    public Element extractContent(BxDocument document) throws AnalysisException {
        Element content = metadataExtractor.extractMetadata(document);
        Element[] references = referencesExtractor.extractReferences(document);
        
        Element back = content.getChild("back");
        Element refList = back.getChild("ref-list");
        for (Element ref : references) {
            Element r = new Element("ref");
            r.addContent(ref);
            refList.addContent(r);
        }
        
        return content;
    }
    
}
