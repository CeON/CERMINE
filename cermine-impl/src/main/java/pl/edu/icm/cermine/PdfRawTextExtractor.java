package pl.edu.icm.cermine;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.*;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;

/**
 * Text extractor from PDF files. Extracted text includes 
 * all text string found in the document in correct reading order.
 *
 * @author Paweł Szostek
 * @author Dominika Tkaczyk
 */
public class PdfRawTextExtractor implements DocumentTextExtractor<String> {
    /** individual character extractor */
    private CharacterExtractor characterExtractor;
    
    /** document object segmenter */
    private DocumentSegmenter documentSegmenter;
    
    /** reading order resolver */
    private ReadingOrderResolver roResolver;
    
    public PdfRawTextExtractor() throws AnalysisException {
        characterExtractor = new ITextCharacterExtractor();
        documentSegmenter = new DocstrumSegmenter();
        roResolver = new HierarchicalReadingOrderResolver();
    }
    
    public PdfRawTextExtractor(CharacterExtractor glyphExtractor, DocumentSegmenter pageSegmenter, ReadingOrderResolver roResolver) {
        this.characterExtractor = glyphExtractor;
        this.documentSegmenter = pageSegmenter;
        this.roResolver = roResolver;
    }
    
    /**
     * Extracts content of a pdf to a plain text.
     * 
     * @param stream
     * @return pdf's content as plain text
     * @throws AnalysisException 
     */
    @Override
    public String extractText(InputStream stream) throws AnalysisException {
        BxDocument doc = characterExtractor.extractCharacters(stream);
        return extractText(doc);
    }
    
    /**
     * Extracts content of a pdf to a plain text.
     * 
     * @param document
     * @return pdf's content as plain text
     * @throws AnalysisException 
     */
    @Override
    public String extractText(BxDocument document) throws AnalysisException {
        BxDocument doc = documentSegmenter.segmentDocument(document);
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
