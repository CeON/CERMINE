package pl.edu.icm.coansys.metaextr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.*;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.tools.BxModelUtils;


/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfGeometricStructureExtractor implements DocumentGeometricStructureExtractor {
    
    CharacterExtractor glyphExtractor;
    
    PageSegmenter pageSegmenter;
    
    ReadingOrderResolver roResolver;
    
    ZoneClassifier initialClassifier;


    public PdfGeometricStructureExtractor() throws IOException {
        glyphExtractor = new ITextCharacterExtractor();
        pageSegmenter = new DocstrumPageSegmenter();
        roResolver = new HierarchicalReadingOrderResolver();
        
        InputStreamReader modelISRI = new InputStreamReader(PdfGeometricStructureExtractor.class
				.getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/svm_initial_classifier"));
        BufferedReader modelFileI = new BufferedReader(modelISRI);
        InputStreamReader rangeISRI = new InputStreamReader(PdfGeometricStructureExtractor.class
		        .getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/svm_initial_classifier.range"));
        BufferedReader rangeFileI = new BufferedReader(rangeISRI);
        initialClassifier = new SVMInitialZoneClassifier(modelFileI, rangeFileI);
    }
        
    public BxDocument extractStructure(InputStream stream) throws AnalysisException {
        BxDocument doc = glyphExtractor.extractCharacters(stream);
        doc = pageSegmenter.segmentPages(doc);
        BxModelUtils.setParents(doc);
        doc = roResolver.resolve(doc);
        return initialClassifier.classifyZones(doc);
    }

    public void setGlyphExtractor(CharacterExtractor glyphExtractor) {
        this.glyphExtractor = glyphExtractor;
    }

    public void setInitialClassifier(ZoneClassifier initialClassifier) {
        this.initialClassifier = initialClassifier;
    }

    public void setPageSegmenter(PageSegmenter pageSegmenter) {
        this.pageSegmenter = pageSegmenter;
    }

    public void setRoResolver(ReadingOrderResolver roResolver) {
        this.roResolver = roResolver;
    }
   
}