package pl.edu.icm.cermine.content.headers;

import java.io.BufferedReader;
import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.*;

/**
 *
 * @author Dominika Tkaczyk
 */
public class SVMContentHeadersExtractor implements ContentHeadersExtractor {

    private SVMHeaderLinesClassifier contentHeaderClassifier;
    
    private HeadersClusterizer headersClusterizer;
    
    private HeaderLinesCompletener headerLinesCompletener;

    public SVMContentHeadersExtractor(SVMHeaderLinesClassifier contentHeaderClassifier) {
        this.contentHeaderClassifier = contentHeaderClassifier;
        this.headersClusterizer = new HeadersClusterizer();
        this.headerLinesCompletener = new HeaderLinesCompletener();
    }
    
    public SVMContentHeadersExtractor(BufferedReader modelFile, BufferedReader rangeFile) throws AnalysisException {
		this.contentHeaderClassifier = new SVMHeaderLinesClassifier(modelFile, rangeFile);
        this.headersClusterizer = new HeadersClusterizer();
        this.headerLinesCompletener = new HeaderLinesCompletener();
	}

	public SVMContentHeadersExtractor(String modelFilePath, String rangeFilePath) throws AnalysisException {
		this.contentHeaderClassifier = new SVMHeaderLinesClassifier(modelFilePath, rangeFilePath);
        this.headersClusterizer = new HeadersClusterizer();
        this.headerLinesCompletener = new HeaderLinesCompletener();
	}
    
   
    private boolean isHeader(BxLine line, BxPage page) {
        BxZoneLabel label = contentHeaderClassifier.predictLabel(line, page);
        return label.equals(BxZoneLabel.BODY_HEADING);
    }
    
    @Override
    public BxDocContentStructure extractHeaders(BxDocument document) throws AnalysisException {

        BxDocContentStructure contentStructure = new BxDocContentStructure();
        BxLine lastHeaderLine = null;
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)) {
                    for (BxLine line : zone.getLines()) {
                        if (isHeader(line, page)) {
                            contentStructure.addFirstHeaderLine(page, line);
                            lastHeaderLine = line;
                        } else if (zone.getLabel().equals(BxZoneLabel.BODY_CONTENT) || zone.getLabel().equals(BxZoneLabel.GEN_BODY)) {
                            contentStructure.addContentLine(lastHeaderLine, line);
                        }
                    }
                }
            }
        }
        
        headersClusterizer.clusterHeaders(contentStructure);
        headerLinesCompletener.completeLines(contentStructure);
        
        return contentStructure;
    }
    
}
