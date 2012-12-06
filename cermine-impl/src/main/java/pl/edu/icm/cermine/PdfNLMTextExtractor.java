package pl.edu.icm.cermine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.jdom.Element;
import pl.edu.icm.cermine.content.LogicalStructureExtractor;
import pl.edu.icm.cermine.content.SVMLogicalStructureExtractor;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.content.transformers.DocContentStructToNLMElementConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * Text extractor from PDF files. Extracted text includes 
 * all text string found in the document in correct reading order.
 *
 * @author Paweł Szostek
 * @author Dominika Tkaczyk
 */
public class PdfNLMTextExtractor implements DocumentTextExtractor<Element> {
    
    /** geometric structure extractor */
    private DocumentStructureExtractor strExtractor;
    
    private LogicalStructureExtractor logicalExtractor;
    
    private ModelToModelConverter<DocumentContentStructure, Element> converter;

    public PdfNLMTextExtractor() throws AnalysisException {
        strExtractor = new PdfBxStructureExtractor();
       
        String filteringModel = "/pl/edu/icm/cermine/content/filtering.model";
        String filteringRange = "/pl/edu/icm/cermine/content/filtering.range";
        String headerModel = "/pl/edu/icm/cermine/content/header.model";
        String headerRange = "/pl/edu/icm/cermine/content/header.range";
        
        BufferedReader br1 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(filteringModel)));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(filteringRange)));
        BufferedReader br3 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(headerModel)));
        BufferedReader br4 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(headerRange)));

        logicalExtractor = new SVMLogicalStructureExtractor(br1, br2, br3, br4);
        
        converter = new DocContentStructToNLMElementConverter();
    }

    public PdfNLMTextExtractor(InputStream filteringModel, InputStream filteringRange, 
            InputStream headerModel, InputStream headerRange) throws AnalysisException {
        strExtractor = new PdfBxStructureExtractor();
        
        BufferedReader br1 = new BufferedReader(new InputStreamReader(filteringModel));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(filteringRange));
        BufferedReader br3 = new BufferedReader(new InputStreamReader(headerModel));
        BufferedReader br4 = new BufferedReader(new InputStreamReader(headerRange));

        logicalExtractor = new SVMLogicalStructureExtractor(br1, br2, br3, br4);
        
        converter = new DocContentStructToNLMElementConverter();
    }
    
    public PdfNLMTextExtractor(DocumentStructureExtractor strExtractor, LogicalStructureExtractor logicalExtractor) {
        this.strExtractor = strExtractor;
        this.logicalExtractor = logicalExtractor;
        this.converter = new DocContentStructToNLMElementConverter();
    }

    @Override
    public Element extractText(InputStream stream) throws AnalysisException {
        return extractText(strExtractor.extractStructure(stream));
    }

    @Override
    public Element extractText(BxDocument document) throws AnalysisException {
        try {
            DocumentContentStructure struct = logicalExtractor.extractStructure(document);
            return converter.convert(struct);
        } catch (TransformationException ex) {
            throw new AnalysisException("Cannot extract text from document!", ex);
        }
    }
    
}
