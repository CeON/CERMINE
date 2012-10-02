package pl.edu.icm.coansys.metaextr;

import java.io.IOException;
import java.io.InputStream;
import pl.edu.icm.coansys.metaextr.bibref.BibReferenceExtractor;
import pl.edu.icm.coansys.metaextr.bibref.BibReferenceParser;
import pl.edu.icm.coansys.metaextr.bibref.CRFBibReferenceParser;
import pl.edu.icm.coansys.metaextr.bibref.ClusteringBibReferenceExtractor;
import pl.edu.icm.coansys.metaextr.bibref.model.BibEntry;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;


/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfReferencesExtractor implements DocumentReferencesExtractor<BibEntry> {
    
    DocumentGeometricStructureExtractor strExtractor;
    
    BibReferenceExtractor extractor;
    
    BibReferenceParser<BibEntry> parser;

    public PdfReferencesExtractor() throws IOException {
        strExtractor = new PdfGeometricStructureExtractor();
        extractor = new ClusteringBibReferenceExtractor();
        parser = new CRFBibReferenceParser();
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
