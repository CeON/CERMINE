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

package pl.edu.icm.cermine;

import com.google.common.collect.Lists;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ContentExtractorLoopTest {
    static final private List<String> TEST_PDFS =
            Lists.newArrayList(
                    "/pl/edu/icm/cermine/test1.pdf",
                    "/pl/edu/icm/cermine/test2.pdf",
                    "/pl/edu/icm/cermine/test3.pdf",
                    "/pl/edu/icm/cermine/test4.pdf",
                    "/pl/edu/icm/cermine/test5.pdf",
                    "/pl/edu/icm/cermine/test6.pdf"
            );

    private ContentExtractor extractor;
    
    @Before
    public void setUp() throws AnalysisException, IOException {
        extractor = new ContentExtractor();
    }

    @Test
    public void extractionLoopTest() throws AnalysisException, IOException {
        List<String> titles = new ArrayList<String>();
        for (String file : TEST_PDFS) {
            InputStream testStream = ContentExtractorLoopTest.class.getResourceAsStream(file);
            extractor.setPDF(testStream);
            extractor.getContentAsNLM();
            titles.add(extractor.getMetadata().getTitle());
        }

        assertEquals(Lists.newArrayList(
                "Complications related to deep venous thrombosis prophylaxis in trauma: a systematic review of the literature",
                "Patient Experiences of Structured Heart Failure Programmes",
                "Phytochemical and Biological investigations of Phoenix paludosa Roxb.",
                "The four Zn fingers of MBNL1 provide a flexible platform for recognition of its RNA binding elements",
                "Iron deficiency anaemia can be improved after eradication of Helicobacter pylori",
                "VESPA: Very large-scale Evolutionary and Selective Pressure Analyses"),
            titles); 
    }

}
