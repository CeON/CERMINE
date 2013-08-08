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

package pl.edu.icm.cermine.content;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.filtering.SVMContentFilter;
import pl.edu.icm.cermine.content.headers.SVMContentHeadersExtractor;
import pl.edu.icm.cermine.content.transformers.BxContentStructToDocContentStructConverter;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class SVMLogicalStructureExtractor extends LogicalStructureExtractor {

    public SVMLogicalStructureExtractor() throws AnalysisException {
        String filteringModel = "/pl/edu/icm/cermine/content/filtering.model";
        String filteringRange = "/pl/edu/icm/cermine/content/filtering.range";
        String headerModel = "/pl/edu/icm/cermine/content/header.model";
        String headerRange = "/pl/edu/icm/cermine/content/header.range";
        
        BufferedReader br1 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(filteringModel)));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(filteringRange)));
        BufferedReader br3 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(headerModel)));
        BufferedReader br4 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(headerRange)));
        
        this.setContentFilter(new SVMContentFilter(br1, br2));
        this.setHeaderExtractor(new SVMContentHeadersExtractor(br3, br4));
        this.setContentCleaner(new ContentCleaner());
        this.setConverter(new BxContentStructToDocContentStructConverter());
    }
    
    public SVMLogicalStructureExtractor(BufferedReader filterModelFile, BufferedReader filterRangeFile,
            BufferedReader headerModelFile, BufferedReader headerRangeFile) throws AnalysisException {
        super(new SVMContentFilter(filterModelFile, filterRangeFile),
                new SVMContentHeadersExtractor(headerModelFile, headerRangeFile),
                new ContentCleaner(),
                new BxContentStructToDocContentStructConverter());
    }
    
}
