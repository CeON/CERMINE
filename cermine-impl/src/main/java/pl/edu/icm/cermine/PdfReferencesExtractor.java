package pl.edu.icm.cermine;

import java.io.InputStream;
import pl.edu.icm.cermine.bibref.BibReferenceExtractor;
import pl.edu.icm.cermine.bibref.BibReferenceParser;
import pl.edu.icm.cermine.bibref.CRFBibReferenceParser;
import pl.edu.icm.cermine.bibref.ClusteringBibReferenceExtractor;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;


/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfReferencesExtractor implements DocumentReferencesExtractor<BibEntry> {
    
    DocumentGeometricStructureExtractor strExtractor;
    
    BibReferenceExtractor extractor;
    
    BibReferenceParser<BibEntry> parser;

    public PdfReferencesExtractor() throws AnalysisException {
        strExtractor = new PdfGeometricStructureExtractor();
        extractor = new ClusteringBibReferenceExtractor();
        InputStream modelFile = Thread.currentThread().getClass().getResourceAsStream("/pl/edu/icm/cermine/bibref/acrf-small.ser.gz");
        parser = new CRFBibReferenceParser(modelFile);
    }
    
    public BibEntry[] extractReferences(InputStream stream) throws AnalysisException {
        BxDocument doc = strExtractor.extractStructure(stream);
        return extractReferences(doc);
    }

    public BibEntry[] extractReferences(BxDocument document) throws AnalysisException {
        String[] refs = extractor.extractBibReferences(document);
        
        BibEntry[] parsedRefs = new BibEntry[refs.length];
        for (int i = 0; i < refs.length; i++) {
            parsedRefs[i] = parser.parseBibReference(refs[i]);
        }
        return parsedRefs;
    }

    public void setExtractor(BibReferenceExtractor extractor) {
        this.extractor = extractor;
    }

    public void setParser(BibReferenceParser<BibEntry> parser) {
        this.parser = parser;
    }

    public void setStrExtractor(DocumentGeometricStructureExtractor strExtractor) {
        this.strExtractor = strExtractor;
    }

}
