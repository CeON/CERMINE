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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.jdom.JDOMException;
import org.junit.Before;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class SVMLogicalStructureExtractorTest extends AbstractLogicalStructureExtractorTest {
    
    String filteringModel = "filtering.model";
    String filteringRange = "filtering.range";
    String headerModel = "header.model";
    String headerRange = "header.range";
    
    
    @Before
    @Override
    public void setUp() throws IOException, TransformationException, AnalysisException, URISyntaxException, JDOMException {
        super.setUp();
        BufferedReader br1 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(dir+filteringModel)));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(dir+filteringRange)));
        BufferedReader br3 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(dir+headerModel)));
        BufferedReader br4 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(dir+headerRange)));

        extractor = new SVMLogicalStructureExtractor(br1, br2, br3, br4);
    }
    
}