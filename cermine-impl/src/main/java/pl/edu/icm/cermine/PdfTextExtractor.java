package pl.edu.icm.cermine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.CharacterExtractor;
import pl.edu.icm.cermine.structure.DocstrumSegmenter;
import pl.edu.icm.cermine.structure.DocumentSegmenter;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.ITextCharacterExtractor;
import pl.edu.icm.cermine.structure.ReadingOrderResolver;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;

public class PdfTextExtractor {
    /** individual character extractor */
    private CharacterExtractor characterExtractor;
    
    /** document object segmenter */
    private DocumentSegmenter documentSegmenter;
    
    /** reading order resolver */
    private ReadingOrderResolver roResolver;
    
    public PdfTextExtractor() throws AnalysisException {
        characterExtractor = new ITextCharacterExtractor();
        documentSegmenter = new DocstrumSegmenter();
        roResolver = new HierarchicalReadingOrderResolver();
    }
    
    public PdfTextExtractor(CharacterExtractor glyphExtractor, DocumentSegmenter pageSegmenter, 
            ReadingOrderResolver roResolver, ZoneClassifier initialClassifier) {
        this.characterExtractor = glyphExtractor;
        this.documentSegmenter = pageSegmenter;
        this.roResolver = roResolver;
    }
    
    /*
     * Extracts content of a pdf to a plain text.
     * 
     * @param stream
     * @return pdf's content as plain text
     * @throws AnalysisException 
     */
    public String extractText(InputStream stream) throws AnalysisException {
        BxDocument doc = characterExtractor.extractCharacters(stream);
        doc = documentSegmenter.segmentDocument(doc);
        BxModelUtils.setParents(doc);
        doc = roResolver.resolve(doc);
        return doc.toText();
    }

    public void setGlyphExtractor(CharacterExtractor glyphExtractor) {
        this.characterExtractor = glyphExtractor;
    }

    public void setPageSegmenter(DocumentSegmenter pageSegmenter) {
        this.documentSegmenter = pageSegmenter;
    }

    public void setRoResolver(ReadingOrderResolver roResolver) {
        this.roResolver = roResolver;
    }
    
}
