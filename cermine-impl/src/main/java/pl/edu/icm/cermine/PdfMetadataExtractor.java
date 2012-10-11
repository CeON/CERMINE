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
 *
 * @author Dominika Tkaczyk
 */
public class PdfMetadataExtractor implements DocumentMetadataExtractor<Element> {

    private DocumentGeometricStructureExtractor strExtractor;
   
    private ZoneClassifier metadataClassifier;
    
    private MetadataExtractor<Element> extractor;

    public PdfMetadataExtractor() throws IOException {
        strExtractor = new PdfGeometricStructureExtractor();
        
        InputStreamReader modelISRM = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream("/pl/edu/icm/cermine/structure/svm_metadata_classifier"));
        BufferedReader modelFileM = new BufferedReader(modelISRM);
        InputStreamReader rangeISRM = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream("/pl/edu/icm/cermine/structure/svm_metadata_classifier.range"));
        BufferedReader rangeFileM = new BufferedReader(rangeISRM);
        metadataClassifier = new SVMMetadataZoneClassifier(modelFileM, rangeFileM);
        
        extractor = new EnhancerMetadataExtractor();
    }
    
    public PdfMetadataExtractor(InputStream model, InputStream range) throws IOException {
        strExtractor = new PdfGeometricStructureExtractor();
        
        InputStreamReader modelISRM = new InputStreamReader(model);
        BufferedReader modelFileM = new BufferedReader(modelISRM);
        InputStreamReader rangeISRM = new InputStreamReader(range);
        BufferedReader rangeFileM = new BufferedReader(rangeISRM);
        metadataClassifier = new SVMMetadataZoneClassifier(modelFileM, rangeFileM);
        
        extractor = new EnhancerMetadataExtractor();
    }
        
    @Override
    public Element extractMetadata(InputStream stream) throws AnalysisException {
        BxDocument doc = strExtractor.extractStructure(stream);
        return extractMetadata(doc);
    }
    
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

    public void setStrExtractor(DocumentGeometricStructureExtractor strExtractor) {
        this.strExtractor = strExtractor;
    }

}