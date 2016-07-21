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

package pl.edu.icm.cermine.content.transformers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.jdom.JDOMException;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.content.model.BxContentStructure.BxDocContentPart;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxBounds;
import pl.edu.icm.cermine.structure.model.BxChunk;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxWord;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxToDocContentStructConverterTest {
    
    BxContentToDocContentConverter converter;
    DocContentToHTMLWriter writer;
    
    String expectedHTML;
    BxContentStructure bxDocStruct;
    
    @Before
    public void setUp() throws JDOMException, IOException, TransformationException, URISyntaxException {
        bxDocStruct = new BxContentStructure();
        
        BxLine line1 = constructLine("1. Section");
        BxLine line2 = constructLine("par1");
        BxLine line3 = constructLine("par2");
        BxLine line4 = constructLine("2. Section");
        BxLine line5 = constructLine("par3");
        BxLine line6 = constructLine("2.1. Subsection");
        BxLine line7 = constructLine("2.2. Subsection");
        BxLine line8 = constructLine("par4");
        BxLine line9 = constructLine("par5");
        BxLine line10 = constructLine("2.2.1. Subsubsection");
        BxLine line11 = constructLine("par6");
        BxLine line12 = constructLine("par7");
        BxLine line13 = constructLine("3. Section");
        BxLine line14 = constructLine("par8");
        BxLine line15 = constructLine("par9");
        BxLine line16 = constructLine("par10");
        
        int[] levelIds = {0, 0, 1, 1, 2, 0};
        
        bxDocStruct.addFirstHeaderLine(null, line1);
        bxDocStruct.addContentLine(line1, line2);
        bxDocStruct.addContentLine(line1, line3);
        bxDocStruct.addFirstHeaderLine(null, line4);
        bxDocStruct.addContentLine(line4, line5);
        bxDocStruct.addFirstHeaderLine(null, line6);
        bxDocStruct.addFirstHeaderLine(null, line7);
        bxDocStruct.addContentLine(line7, line8);
        bxDocStruct.addContentLine(line7, line9);
        bxDocStruct.addFirstHeaderLine(null, line10);
        bxDocStruct.addContentLine(line10, line11);
        bxDocStruct.addContentLine(line10, line12);
        bxDocStruct.addFirstHeaderLine(null, line13);
        bxDocStruct.addContentLine(line13, line14);
        bxDocStruct.addContentLine(line13, line15);
        bxDocStruct.addContentLine(line13, line16);
        
        bxDocStruct.setHeaderLevelIds(levelIds);

        for (BxDocContentPart part : bxDocStruct.getParts()) {
            part.setCleanHeaderText(part.getFirstHeaderLine().toText());
            List<String> contentTexts = new ArrayList<String>();
            for (BxLine contentLine : part.getContentLines()) {
                contentTexts.add(contentLine.toText());
            }
            part.setCleanContentTexts(contentTexts);
        }
        
        expectedHTML = "<html><H1>1. Section</H1><p>par1</p><p>par2</p>"
            +"<H1>2. Section</H1><p>par3</p>"
            +"<H2>2.1. Subsection</H2>"
            +"<H2>2.2. Subsection</H2><p>par4</p><p>par5</p>"
            +"<H3>2.2.1. Subsubsection</H3><p>par6</p><p>par7</p>"
            +"<H1>3. Section</H1><p>par8</p><p>par9</p><p>par10</p></html>";
        
        writer = new DocContentToHTMLWriter();
        converter = new BxContentToDocContentConverter();
    }

    @Test
    public void structToXMLTest() throws SAXException, IOException, TransformationException, JDOMException {
        ContentStructure dcs = converter.convert(bxDocStruct);
        String dcsHTML = writer.write(dcs);
        
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expectedHTML, dcsHTML);
        
        assertTrue(diff.similar());
    }
    
    private BxLine constructLine(String text) {
        BxChunk chunk = new BxChunk(new BxBounds(), text);
        BxWord word = new BxWord().addChunk(chunk);
        return new BxLine().addWord(word);
    }
}
