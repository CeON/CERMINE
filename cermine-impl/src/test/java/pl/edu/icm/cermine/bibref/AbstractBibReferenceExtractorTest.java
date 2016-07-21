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

package pl.edu.icm.cermine.bibref;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipFile;
import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class AbstractBibReferenceExtractorTest {
    static final private String TEST_FILE = "/pl/edu/icm/cermine/bibref/refs.xml.zip";
    static final private String EXP_FILE = "/pl/edu/icm/cermine/bibref/refs.txt";
    
    private TrueVizToBxDocumentReader bxReader;
    
    @Before
    public void setUp() {
        bxReader = new TrueVizToBxDocumentReader();
    }
    
    @Test
    public void metadataExtractionTest() throws AnalysisException, JDOMException, IOException, SAXException, TransformationException, URISyntaxException {
        InputStream expStream = this.getClass().getResourceAsStream(EXP_FILE);
        BufferedReader expReader = new BufferedReader(new InputStreamReader(expStream));
        
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = expReader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        expStream.close();
        expReader.close();
        
        URL url = this.getClass().getResource(TEST_FILE);
        ZipFile zipFile = new ZipFile(new File(url.toURI()));
        InputStream inputStream = zipFile.getInputStream(zipFile.getEntry("out.xml"));
        BxDocument expDocument = new BxDocument().setPages(bxReader.read(new InputStreamReader(inputStream)));
        String[] references = getExtractor().extractBibReferences(expDocument);
        
        assertEquals(StringUtils.join(references, "\n"), sb.toString().trim());
    }
    
    protected abstract BibReferenceExtractor getExtractor();
}
