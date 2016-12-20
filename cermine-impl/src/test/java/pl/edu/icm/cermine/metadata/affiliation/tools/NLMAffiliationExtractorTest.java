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
package pl.edu.icm.cermine.metadata.affiliation.tools;

import java.io.IOException;
import java.util.List;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.xml.sax.InputSource;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.transformers.MetadataToNLMConverter;

/**
 * @author Bartosz Tarnawski
 */
public class NLMAffiliationExtractorTest {

    private final NLMAffiliationExtractor instance = new NLMAffiliationExtractor();

    @Test
    public void testExtractStrings() throws JDOMException, IOException, TransformationException {
        InputSource source = new InputSource(NLMAffiliationExtractor.class.getResourceAsStream(
                "test-nlm-extract-affs.xml"));

        String[] expectedString = {
            "<aff id=\"\"><label></label><institution>School</institution><institution>of</institution><institution>Biological</institution><institution>and</institution><institution>Chemical</institution><institution>Sciences</institution><institution>,</institution><institution>Queen</institution><institution>Mary</institution><institution>University</institution><institution>of</institution><institution>London</institution>,<addr-line>London</addr-line>,<country country=\"UK\">UK</country></aff>",
            "<aff id=\"\"><label></label><institution>Department</institution><institution>of</institution><institution>Pathology</institution><institution>,</institution><institution>University</institution><institution>of</institution><institution>Cincinnati</institution><institution>College</institution><institution>of</institution><institution>Medicine</institution>,<country country=\"US\">USA</country></aff>"
        };

        MetadataToNLMConverter converter = new MetadataToNLMConverter();
        XMLOutputter outputter = new XMLOutputter();
        List<DocumentAffiliation> affs = instance.extractStrings(source);
        assertEquals(affs.size(), 2);
        for (int i = 0; i < 2; i++) {
            Element element = converter.convertAffiliation(affs.get(i));
            String actual = outputter.outputString(element);
            String expected = expectedString[i];
            assertEquals(expected, actual);
        }
    }
}
