package pl.edu.icm.cermine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.jdom.Element;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.EnhancerMetadataExtractor;
import pl.edu.icm.cermine.metadata.MetadataExtractor;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;


/**
 * NLM-based metadata extractor from PDF files.
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMMetadataExtractor implements DocumentMetadataExtractor<Element> {

    /** geometric structure extractor */
    private DocumentStructureExtractor strExtractor;
   
    /** metadata zone classifier */
    private ZoneClassifier metadataClassifier;
    
    /** metadata extractor from labelled zones */
    private MetadataExtractor<Element> extractor;

    public PdfNLMMetadataExtractor() throws AnalysisException, IOException {
        strExtractor = new PdfBxStructureExtractor();
        
        InputStreamReader modelISRM = new InputStreamReader(this.getClass()
				.getResourceAsStream("/pl/edu/icm/cermine/structure/svm_metadata_classifier"));
        BufferedReader modelFileM = new BufferedReader(modelISRM);
        InputStreamReader rangeISRM = new InputStreamReader(this.getClass()
				.getResourceAsStream("/pl/edu/icm/cermine/structure/svm_metadata_classifier.range"));
        BufferedReader rangeFileM = new BufferedReader(rangeISRM);
        metadataClassifier = new SVMMetadataZoneClassifier(modelFileM, rangeFileM);
        
        extractor = new EnhancerMetadataExtractor();
    }
    
    public PdfNLMMetadataExtractor(InputStream model, InputStream range) throws AnalysisException, IOException {
        strExtractor = new PdfBxStructureExtractor();
        
        InputStreamReader modelISRM = new InputStreamReader(model);
        BufferedReader modelFileM = new BufferedReader(modelISRM);
        InputStreamReader rangeISRM = new InputStreamReader(range);
        BufferedReader rangeFileM = new BufferedReader(rangeISRM);
        metadataClassifier = new SVMMetadataZoneClassifier(modelFileM, rangeFileM);
        
        extractor = new EnhancerMetadataExtractor();
    }

    public PdfNLMMetadataExtractor(DocumentStructureExtractor strExtractor, ZoneClassifier metadataClassifier, 
            MetadataExtractor<Element> extractor) {
        this.strExtractor = strExtractor;
        this.metadataClassifier = metadataClassifier;
        this.extractor = extractor;
    }
         
    /**
     * Extracts metadata from PDF file and stores it in NLM format.
     * 
     * @param stream
     * @return extracted metadata in NLM format
     * @throws AnalysisException 
     */
    @Override
    public Element extractMetadata(InputStream stream) throws AnalysisException {
        BxDocument doc = strExtractor.extractStructure(stream);
        return extractMetadata(doc);
    }
    
    /**
     * Extracts metadata from PDF file and stores it in NLM format.
     * 
     * @param document
     * @return extracted metadata in NLM format
     * @throws AnalysisException 
     */
    @Override
    public Element extractMetadata(BxDocument document) throws AnalysisException {
        BxDocument doc = metadataClassifier.classifyZones(document);
        return extractor.extractMetadata(doc);
    }

    public void setExtractor(MetadataExtractor<Element> extractor) {
        this.extractor = extractor;
    }

    public void setMetadataClassifier(ZoneClassifier metadataClassifier) {
        this.metadataClassifier = metadataClassifier;
    }

    public void setStrExtractor(DocumentStructureExtractor strExtractor) {
        this.strExtractor = strExtractor;
    }

}