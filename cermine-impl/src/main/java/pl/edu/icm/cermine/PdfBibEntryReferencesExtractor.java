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
public class PdfBibEntryReferencesExtractor implements DocumentReferencesExtractor<BibEntry> {
    
    private DocumentStructureExtractor strExtractor;
    
    private BibReferenceExtractor extractor;
    
    private BibReferenceParser<BibEntry> parser;

    public PdfBibEntryReferencesExtractor() throws AnalysisException {
        strExtractor = new PdfBxStructureExtractor();
        extractor = new ClusteringBibReferenceExtractor();
        InputStream modelFile = Thread.currentThread().getClass().getResourceAsStream("/pl/edu/icm/cermine/bibref/acrf-small.ser.gz");
        parser = new CRFBibReferenceParser(modelFile);
    }
    
    public PdfBibEntryReferencesExtractor(InputStream model) throws AnalysisException {
        strExtractor = new PdfBxStructureExtractor();
        extractor = new ClusteringBibReferenceExtractor();
        parser = new CRFBibReferenceParser(model);
    }

    public PdfBibEntryReferencesExtractor(DocumentStructureExtractor strExtractor, BibReferenceExtractor extractor, BibReferenceParser<BibEntry> parser) {
        this.strExtractor = strExtractor;
        this.extractor = extractor;
        this.parser = parser;
    }
    
    
    @Override
    public BibEntry[] extractReferences(InputStream stream) throws AnalysisException {
        BxDocument doc = strExtractor.extractStructure(stream);
        return extractReferences(doc);
    }

    @Override
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

    public void setStrExtractor(DocumentStructureExtractor strExtractor) {
        this.strExtractor = strExtractor;
    }

}
