package pl.edu.icm.cermine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.xpath.XPathExpressionException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * NLM-based content extractor from PDF files.
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMContentExtractor implements DocumentContentExtractor<Element> {

    /** geometric structure extractor */
    private DocumentStructureExtractor structureExtractor;
    
    /** document metadata extractor from geometric structure */
    private DocumentMetadataExtractor<Element> metadataExtractor;
    
    /** parsed references extractor from geometric structure */
    private DocumentReferencesExtractor<Element> referencesExtractor;
    
    /** logical content extractor */
    private DocumentTextExtractor<Element> textExtractor;
    
    private boolean extractMetadata = true;
    
    private boolean extractReferences = true;
    
    private boolean extractText = true;
           

    public PdfNLMContentExtractor() throws AnalysisException {
        structureExtractor = new PdfBxStructureExtractor();
        metadataExtractor = new PdfNLMMetadataExtractor();
        referencesExtractor = new PdfNLMReferencesExtractor();
        textExtractor = new PdfNLMTextExtractor();
    }

    public PdfNLMContentExtractor(DocumentStructureExtractor structureExtractor, DocumentMetadataExtractor<Element> metadataExtractor, 
            DocumentReferencesExtractor<Element> referencesExtractor, DocumentTextExtractor<Element> textExtractor) {
        this.structureExtractor = structureExtractor;
        this.metadataExtractor = metadataExtractor;
        this.referencesExtractor = referencesExtractor;
        this.textExtractor = textExtractor;
    }
    
    
    /**
     * Extracts content from PDF file and stores it in NLM format.
     * 
     * @param stream
     * @return extracted content in NLM format
     * @throws AnalysisException 
     */
    @Override
    public Element extractContent(InputStream stream) throws AnalysisException {
        BxDocument document = structureExtractor.extractStructure(stream);
        return extractContent(document);
    }

    /**
     * Extracts content from PDF file and stored it in NLM format.
     * 
     * @param document
     * @return extracted content in NLM format
     * @throws AnalysisException 
     */
    @Override
    public Element extractContent(BxDocument document) throws AnalysisException {
        Element content = new Element("article");
        
        Element metadata = new Element("front");
        if (extractMetadata) {
            metadata = (Element) metadataExtractor.extractMetadata(document).getChild("front").clone();
        }
        content.addContent(metadata);
        
        Element text = new Element("body");
        if (extractText) {
            text = textExtractor.extractText(document);
        }
        content.addContent(text);
        
        Element back = new Element("back");
        Element refList = new Element("ref-list");
        if (extractReferences) {
            Element[] references = referencesExtractor.extractReferences(document);
            for (Element ref : references) {
                Element r = new Element("ref");
                r.addContent(ref);
                refList.addContent(r);
            }
        }
        back.addContent(refList);
        content.addContent(back);

        return content;
    }
    

    public void buildStructureExtractor(InputStream initialModel, InputStream initialRange) throws AnalysisException {
        structureExtractor = new PdfBxStructureExtractor(initialModel, initialRange);
    }
    
    public void buildMetadataExtractor(InputStream metadataModel, InputStream metadataRange) throws AnalysisException {
        metadataExtractor = new PdfNLMMetadataExtractor(metadataModel, metadataRange);
    }
    
    public void buildReferencesExtractor(InputStream refModel) throws AnalysisException {
        referencesExtractor = new PdfNLMReferencesExtractor(refModel);
    }
    
    public void buildTextExtractor(InputStream filteringModel, InputStream filteringRange, 
            InputStream headerModel, InputStream headerRange) throws AnalysisException {
        textExtractor = new PdfNLMTextExtractor(filteringModel, filteringRange, headerModel, headerRange);
    }
    
    public PdfNLMContentExtractor(InputStream initialModel, InputStream initialRange, InputStream metadataModel, 
            InputStream metadataRange, InputStream refModel, InputStream filteringModel, InputStream filteringRange, 
            InputStream headerModel, InputStream headerRange) throws AnalysisException {
        structureExtractor = new PdfBxStructureExtractor(initialModel, initialRange);
        metadataExtractor = new PdfNLMMetadataExtractor(metadataModel, metadataRange);
        referencesExtractor = new PdfNLMReferencesExtractor(refModel);
        textExtractor = new PdfNLMTextExtractor(filteringModel, filteringRange, headerModel, headerRange);
    }

    public boolean isExtractMetadata() {
        return extractMetadata;
    }

    public void setExtractMetadata(boolean extractMetadata) {
        this.extractMetadata = extractMetadata;
    }

    public boolean isExtractReferences() {
        return extractReferences;
    }

    public void setExtractReferences(boolean extractReferences) {
        this.extractReferences = extractReferences;
    }

    public boolean isExtractText() {
        return extractText;
    }

    public void setExtractText(boolean extractText) {
        this.extractText = extractText;
    }

    public DocumentMetadataExtractor<Element> getMetadataExtractor() {
        return metadataExtractor;
    }

    public void setMetadataExtractor(DocumentMetadataExtractor<Element> metadataExtractor) {
        this.metadataExtractor = metadataExtractor;
    }

    public DocumentReferencesExtractor<Element> getReferencesExtractor() {
        return referencesExtractor;
    }

    public void setReferencesExtractor(DocumentReferencesExtractor<Element> referencesExtractor) {
        this.referencesExtractor = referencesExtractor;
    }

    public DocumentStructureExtractor getStructureExtractor() {
        return structureExtractor;
    }

    public void setStructureExtractor(DocumentStructureExtractor structureExtractor) {
        this.structureExtractor = structureExtractor;
    }

    public DocumentTextExtractor<Element> getTextExtractor() {
        return textExtractor;
    }

    public void setTextExtractor(DocumentTextExtractor<Element> textExtractor) {
        this.textExtractor = textExtractor;
    }
    
    public static void main(String[] args) throws AnalysisException, XPathExpressionException, JDOMException, IOException {
    	if(args.length != 1){
    		System.err.println("USAGE: program FILE_PATH");
    		System.exit(1);
    	}

    	PdfNLMContentExtractor extractor = new PdfNLMContentExtractor();
    	InputStream in = new FileInputStream(args[0]);
    	Element result = extractor.extractContent(in);

    	XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
    	System.out.println(outputter.outputString(result));
    }
}
