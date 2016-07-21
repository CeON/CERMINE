/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
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
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.*;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SVMContentHeadersExtractor implements ContentHeadersExtractor {

    private static final String MODEL_FILE_PATH = "/pl/edu/icm/cermine/content/header.model";
    private static final String RANGE_FILE_PATH = "/pl/edu/icm/cermine/content/header.range";
    
    private SVMHeaderLinesClassifier contentHeaderClassifier;
    
    private SingleLinkageHeadersClusterizer headersClusterizer;
    
    private HeaderLinesCompletener headerLinesCompletener;

    public SVMContentHeadersExtractor() throws AnalysisException {
        this(MODEL_FILE_PATH, RANGE_FILE_PATH);
    }
    
    public SVMContentHeadersExtractor(SVMHeaderLinesClassifier contentHeaderClassifier) {
        this.contentHeaderClassifier = contentHeaderClassifier;
        this.headersClusterizer = new SingleLinkageHeadersClusterizer();
        this.headerLinesCompletener = new HeaderLinesCompletener();
    }
    
    public SVMContentHeadersExtractor(BufferedReader modelFile, BufferedReader rangeFile) throws AnalysisException {
		this.contentHeaderClassifier = new SVMHeaderLinesClassifier(modelFile, rangeFile);
        this.headersClusterizer = new SingleLinkageHeadersClusterizer();
        this.headerLinesCompletener = new HeaderLinesCompletener();
	}

	public SVMContentHeadersExtractor(String modelFilePath, String rangeFilePath) throws AnalysisException {
		this.contentHeaderClassifier = new SVMHeaderLinesClassifier(modelFilePath, rangeFilePath);
        this.headersClusterizer = new SingleLinkageHeadersClusterizer();
        this.headerLinesCompletener = new HeaderLinesCompletener();
	}
    
   
    private boolean isHeader(BxLine line, BxPage page) {
        BxZoneLabel label = contentHeaderClassifier.predictLabel(line, page);
        return label.equals(BxZoneLabel.BODY_HEADING);
    }
    
    @Override
    public BxContentStructure extractHeaders(BxDocument document) throws AnalysisException {

        BxContentStructure contentStructure = new BxContentStructure();
        BxLine lastHeaderLine = null;
        for (BxPage page : document) {
            for (BxZone zone : page) {
                if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)) {
                    for (BxLine line : zone) {
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
