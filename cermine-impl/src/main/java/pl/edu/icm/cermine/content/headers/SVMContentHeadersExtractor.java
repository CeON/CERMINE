/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

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
                            if (lastHeaderLine == null) {
                                BxChunk chunk = new BxChunk(new BxBounds(), "--");
                                BxWord word = new BxWord().addChunk(chunk);
                                lastHeaderLine = new BxLine().addWord(word);
                                contentStructure.addFirstHeaderLine(page, lastHeaderLine);
                            }
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
