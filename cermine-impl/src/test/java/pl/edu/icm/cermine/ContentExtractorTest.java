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

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
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
import pl.edu.icm.cermine.structure.model.BxImage;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ContentExtractorTest {
    static final private String TEST_PDF_1 = "/pl/edu/icm/cermine/test1.pdf";
    static final private String EXP_STR_1 = "/pl/edu/icm/cermine/test1-structure.xml";
    static final private String EXP_STR_GEN_1 = "/pl/edu/icm/cermine/test1-structure-general.xml";
    static final private String EXP_STR_SPE_1 = "/pl/edu/icm/cermine/test1-structure-specific.xml";
    static final private String EXP_MET_1 = "/pl/edu/icm/cermine/test1-metadata.xml";
   
    static final private String TEST_PDF_2 = "/pl/edu/icm/cermine/test2.pdf";
    static final private String EXP_CONT_2 = "/pl/edu/icm/cermine/test2-content.xml";
    static final private String EXP_TEXT_2 = "/pl/edu/icm/cermine/test2-fulltext.txt";
    static final private String EXP_ZONES_2 = "/pl/edu/icm/cermine/test2-zones.xml";

    static final private String TEST_PDF_4 = "/pl/edu/icm/cermine/test4.pdf";
    static final private String EXP_CONT_4 = "/pl/edu/icm/cermine/test4-content-images.xml";
    static final private String EXP_IMGS_4 = "/pl/edu/icm/cermine/test4-images/";
   
    static final private String TEST_PDF_5 = "/pl/edu/icm/cermine/test5.pdf";
    static final private String EXP_CONT_5 = "/pl/edu/icm/cermine/test5-content.xml";
   
    private ContentExtractor extractor;
    SAXBuilder saxBuilder;
    
    @Before
    public void setUp() throws AnalysisException, IOException {
        extractor = new ContentExtractor();
        saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
    }

    @Test
    public void getBxDocumentTest() throws IOException, AnalysisException, URISyntaxException, TransformationException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_1);
        BxDocument testDocument;
        try {
            extractor.setPDF(testStream);
            testDocument = extractor.getBxDocument();
        } finally {
            testStream.close();
        }

        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_STR_1);
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument expDocument = new BxDocument().setPages(reader.read(new InputStreamReader(expStream, "UTF-8")));
        
        assertTrue(BxModelUtils.areEqual(expDocument, testDocument));
    }

    @Test
    public void getBxDocumentWithGeneralLabelsTest() throws IOException, AnalysisException, URISyntaxException, TransformationException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_1);
        BxDocument testDocument;
        try {
            extractor.setPDF(testStream);
            testDocument = extractor.getBxDocumentWithGeneralLabels();
        } finally {
            testStream.close();
        }

        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_STR_GEN_1);
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument expDocument = new BxDocument().setPages(reader.read(new InputStreamReader(expStream, "UTF-8")));
        
        assertTrue(BxModelUtils.areEqual(expDocument, testDocument));
    }
    
    @Test
    public void getBxDocumentWithSpecificLabelsTest() throws IOException, AnalysisException, URISyntaxException, TransformationException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_1);
        BxDocument testDocument;
        try {
            extractor.setPDF(testStream);
            testDocument = extractor.getBxDocumentWithSpecificLabels();
        } finally {
            testStream.close();
        }

        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_STR_SPE_1);
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument expDocument = new BxDocument().setPages(reader.read(new InputStreamReader(expStream, "UTF-8")));
        
        assertTrue(BxModelUtils.areEqual(expDocument, testDocument));
    }
    
    @Test
    public void textRawFullTextTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_2);
        String testContent;
        try {
            extractor.setPDF(testStream);
            testContent = extractor.getRawFullText();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_TEXT_2);
        BufferedReader reader = null;
        StringBuilder expectedContent = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(expStream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                expectedContent.append(line);
                expectedContent.append("\n");
            }
        } finally {
            try {
                expStream.close();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        
        assertEquals(testContent.trim(), expectedContent.toString().trim());
    }
    
    @Test
    public void getFullTextWithLabelsTest() throws AnalysisException, IOException, JDOMException, SAXException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_2);
        Element testContent;
        try {
            extractor.setPDF(testStream);
            testContent = extractor.getLabelledFullText();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_ZONES_2);
        InputStreamReader expReader = null;
        Document dom;
        try {
            expReader = new InputStreamReader(expStream, "UTF-8");
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            if (expReader != null) {
                expReader.close();
            }
        }
        Element expMetadata = dom.getRootElement();
                
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Diff diff = new Diff(outputter.outputString(expMetadata), outputter.outputString(testContent));
        assertTrue(diff.similar());
    }
    
    @Test
    public void getNLMMetadataTest() throws AnalysisException, IOException, JDOMException, SAXException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_1);
        Element testMetadata;
        try {
            extractor.setPDF(testStream);
            testMetadata = extractor.getMetadataAsNLM();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_MET_1);
        InputStreamReader expReader = null;
        Document dom;
        try {
            expReader = new InputStreamReader(expStream, "UTF-8");
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            if (expReader != null) {
                expReader.close();
            }
        }
        Element expMetadata = dom.getRootElement();
                
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Diff diff = new Diff(outputter.outputString(expMetadata), outputter.outputString(testMetadata));
        assertTrue(diff.similar());
    }

    @Test
    public void getNLMBodyTest() throws AnalysisException, IOException, JDOMException, SAXException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_2);
        Element testBody;
        try {
            extractor.setPDF(testStream);
            testBody = extractor.getBodyAsNLM();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_CONT_2);
        InputStreamReader expReader = null;
        Document dom;
        try {
            expReader = new InputStreamReader(expStream, "UTF-8");
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            if (expReader != null) {
                expReader.close();
            }
        }
        Element expMetadata = dom.getRootElement();
        Element expContent = expMetadata.getChild("body");
                
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Diff diff = new Diff(outputter.outputString(expContent), outputter.outputString(testBody));
        assertTrue(diff.similar());
    }
    
    @Test
    public void getNLMReferencesTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_2);
        List<Element> testReferences;
        try {
            extractor.setPDF(testStream);
            testReferences = extractor.getReferencesAsNLM();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_CONT_2);
        InputStreamReader expReader = null;
        Document dom;
        try {
            expReader = new InputStreamReader(expStream, "UTF-8");
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            if (expReader != null) {
                expReader.close();
            }
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
    }
    
    @Test
    public void getNLMContentTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_2);
        Element testContent;
        try {
            extractor.setPDF(testStream);
            testContent = extractor.getContentAsNLM();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_CONT_2);
        InputStreamReader expReader = null;
        Document dom;
        try {
            expReader = new InputStreamReader(expStream, "UTF-8");
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            if (expReader != null) {
                expReader.close();
            }
        }
        Element expContent = dom.getRootElement();
        
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Diff diff = new Diff(outputter.outputString(expContent), outputter.outputString(testContent));
        assertTrue(diff.similar());
    }

        @Test
    public void getNLMContentRefsTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_5);
        Element testContent;
        try {
            extractor.setPDF(testStream);
            testContent = extractor.getContentAsNLM();
        } finally {
            testStream.close();
        }
        
        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_CONT_5);
        InputStreamReader expReader = null;
        Document dom;
        try {
            expReader = new InputStreamReader(expStream, "UTF-8");
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            if (expReader != null) {
                expReader.close();
            }
        }
        Element expContent = dom.getRootElement();
        
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Diff diff = new Diff(outputter.outputString(expContent), outputter.outputString(testContent));
        assertTrue(diff.similar());
    }
    
    @Test
    public void getImagesTest() throws IOException, AnalysisException, JDOMException, SAXException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_4);
        List<BxImage> images;
        try {
            extractor.setPDF(testStream);
            images = extractor.getImages("pref");
        } finally {
            testStream.close();
        }
        
        assertEquals(4, images.size());
        Collections.sort(images, new Comparator<BxImage>(){
            @Override
            public int compare(BxImage o1, BxImage o2) {
                return o1.getFilename().compareTo(o2.getFilename());
            }
        });
        
        List<String> files = IOUtils.readLines(ContentExtractorTest.class.getResourceAsStream(EXP_IMGS_4), "UTF-8");
        Collections.sort(files);
        for (int i = 0; i < files.size(); i++) {
            String fileName = files.get(i);
            BxImage image = images.get(i);
            assertEquals(fileName, image.getFilename());
            BufferedImage expImg = ImageIO.read(ContentExtractorTest.class.getResourceAsStream(EXP_IMGS_4+fileName));
            BufferedImage testImg = image.getImage();
            assertEquals(expImg.getHeight(), testImg.getHeight());
            assertEquals(expImg.getWidth(), testImg.getWidth());
            for (int y = 0; y < expImg.getHeight(); y++) {
                for (int x = 0; x < expImg.getWidth(); x++) {
                    assertEquals(expImg.getRGB(x, y), testImg.getRGB(x, y));
                }
            }   
        }
    }
    
    @Test
    public void getContentWithImagesTest() throws IOException, AnalysisException, JDOMException, SAXException {
        InputStream testStream = ContentExtractorTest.class.getResourceAsStream(TEST_PDF_4);
        Element testContent;
        try {
            extractor.setPDF(testStream);
            testContent = extractor.getContentAsNLM("pref");
        } finally {
            testStream.close();
        }
        
        InputStream expStream = ContentExtractorTest.class.getResourceAsStream(EXP_CONT_4);
        InputStreamReader expReader = null;
        Document dom;
        try {
            expReader = new InputStreamReader(expStream, "UTF-8");
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            if (expReader != null) {
                expReader.close();
            }
        }
        Element expContent = dom.getRootElement();
        
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        Diff diff = new Diff(outputter.outputString(expContent), outputter.outputString(testContent));
        assertTrue(diff.similar());
    }

}
