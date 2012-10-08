package pl.edu.icm.coansys.metaextr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.jdom.Element;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.metadata.EnhancerMetadataExtractor;
import pl.edu.icm.coansys.metaextr.metadata.MetadataExtractor;
import pl.edu.icm.coansys.metaextr.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.coansys.metaextr.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.coansys.metaextr.structure.ZoneClassifier;
import pl.edu.icm.coansys.metaextr.structure.ZoneClassifier;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;


/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfMetadataExtractor implements DocumentMetadataExtractor<Element> {

    DocumentGeometricStructureExtractor strExtractor;
   
    ZoneClassifier metadataClassifier;
    
    MetadataExtractor<Element> extractor;

    public PdfMetadataExtractor() throws IOException {
        strExtractor = new PdfGeometricStructureExtractor();
        
        InputStreamReader modelISRM = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/svm_metadata_classifier"));
        BufferedReader modelFileM = new BufferedReader(modelISRM);
        InputStreamReader rangeISRM = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/svm_metadata_classifier.range"));
        BufferedReader rangeFileM = new BufferedReader(rangeISRM);
        metadataClassifier = new SVMMetadataZoneClassifier(modelFileM, rangeFileM);
        
        extractor = new EnhancerMetadataExtractor();
    }
        
    public Element extractMetadata(InputStream stream) throws AnalysisException {
        BxDocument doc = strExtractor.extractStructure(stream);
        return extractMetadata(doc);
    }
    
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