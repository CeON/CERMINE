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

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;
import org.custommonkey.xmlunit.Diff;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ContentExtractorTest {
    static final private String TEST_PDF_1 = "/pl/edu/icm/cermine/test1.pdf";
    static final private String EXP_STR_1 = "test1-str.xml";
    static final private String EXP_ZIP_STR_1 = "/pl/edu/icm/cermine/test1-str.xml.zip";
    static final private String EXP_MET_1 = "/pl/edu/icm/cermine/test1-met.xml";
   
    static final private String TEST_PDF_2 = "/pl/edu/icm/cermine/test2.pdf";
    static final private String EXP_CONTENT_2 = "/pl/edu/icm/cermine/test2-cont.xml";
    static final private String EXP_TEXT_2 = "/pl/edu/icm/cermine/test2.txt";
    
    private ContentExtractor extractor;
    
    @Before
    public void setUp() throws AnalysisException, IOException {
        extractor = new ContentExtractor();
    }
  
    @Test
    public void getBxDocumentTest() throws IOException, AnalysisException, URISyntaxException, TransformationException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_PDF_1);
        BxDocument testDocument;
        try {
            extractor.setPDF(testStream);
            testDocument = extractor.getBxDocument();
        } finally {
            testStream.close();
        }
        
        URL url = this.getClass().getResource(EXP_ZIP_STR_1);
        ZipFile zipFile = new ZipFile(new File(url.toURI()));
        InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(EXP_STR_1));
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument expDocument = new BxDocument().setPages(reader.read(new InputStreamReader(inputStream)));
        
        assertTrue(BxModelUtils.areEqual(expDocument, testDocument));
        assertEquals(testDocument, extractor.getBxDocument());
    }
    
    @Test
    public void textRawFullTextTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_PDF_2);
        String testContent;
        try {
            extractor.setPDF(testStream);
            testContent = extractor.getRawFullText();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = this.getClass().getResourceAsStream(EXP_TEXT_2);
        InputStreamReader expReader = new InputStreamReader(expStream);
        BufferedReader reader = new BufferedReader(expReader);
        
        String line;
        StringBuilder expectedContent = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                expectedContent.append(line);
                expectedContent.append("\n");
            }
        } finally {
            expStream.close();
            expReader.close();
            reader.close();
        }
        
        assertEquals(testContent.trim(), expectedContent.toString().trim());
        assertEquals(testContent, extractor.getRawFullText());
    }

    @Test
    public void getNLMMetadataTest() throws AnalysisException, IOException, JDOMException, SAXException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_PDF_1);
        Element testMetadata;
        try {
            extractor.setPDF(testStream);
            testMetadata = extractor.getNLMMetadata();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = this.getClass().getResourceAsStream(EXP_MET_1);
        InputStreamReader expReader = new InputStreamReader(expStream);
        SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        Document dom;
        try {
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            expReader.close();
        }
        Element expMetadata = dom.getRootElement();
                
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Diff diff = new Diff(outputter.outputString(expMetadata), outputter.outputString(testMetadata));
        assertTrue(diff.similar());
        assertEquals(testMetadata, extractor.getNLMMetadata());
    }
    
    @Test
    public void getNLMReferencesTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_PDF_2);
        List<Element> testReferences;
        try {
            extractor.setPDF(testStream);
            testReferences = extractor.getNLMReferences();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = this.getClass().getResourceAsStream(EXP_CONTENT_2);
        InputStreamReader expReader = new InputStreamReader(expStream);
        SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        Document dom;
        try {
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            expReader.close();
        }
        Element expNLM = dom.getRootElement();
        Element back = expNLM.getChild("back");
        Element refList = back.getChild("ref-list");
        
        List<Element> expReferences = new ArrayList<Element>();
        for (Object ref : refList.getChildren("ref")) {
            if (ref instanceof Element) {
                Element mixedCitation = ((Element)ref).getChild("mixed-citation");
                expReferences.add(mixedCitation);
            }
        }
        
        assertEquals(testReferences.size(), expReferences.size());
        
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        for (int i = 0; i < testReferences.size(); i++) {
            Diff diff = new Diff(outputter.outputString(testReferences.get(i)), outputter.outputString(expReferences.get(i)));
            assertTrue(diff.similar());
        }
        assertEquals(testReferences, extractor.getNLMReferences());
    }
    
    @Test
    public void getNLMContentTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = this.getClass().getResourceAsStream(TEST_PDF_2);
        Element testContent;
        try {
            extractor.setPDF(testStream);
            testContent = extractor.getNLMContent();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = this.getClass().getResourceAsStream(EXP_CONTENT_2);
        InputStreamReader expReader = new InputStreamReader(expStream);
        SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        Document dom;
        try {
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            expReader.close();
        }
        Element expContent = dom.getRootElement();
        
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Diff diff = new Diff(outputter.outputString(expContent), outputter.outputString(testContent));
        assertTrue(diff.similar());
        assertEquals(testContent, extractor.getNLMContent());
    }

}
