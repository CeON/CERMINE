package pl.edu.icm.cermine.metadata.affiliations.tools;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import org.xml.sax.InputSource;

import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.tools.TokenizedTextToNLMExporter;

public class NLMAffiliationExtractorTest {

	private NLMAffiliationExtractor instance = new NLMAffiliationExtractor();
	private XMLOutputter outputter = new XMLOutputter();
	
	@Test
	public void testExtractStrings() throws JDOMException, IOException, TransformationException {
        InputSource source = new InputSource(NLMAffiliationExtractor.class.getResourceAsStream(
        		"test-nlm-extract-affs.xml"));
        
        String[] expectedString = {
        		"<aff> <institution>School of Biological and Chemical Sciences, " +
        		"Queen Mary University of London</institution>, <addr-line>London</addr-line>, " +
        		"<country>UK</country></aff>",
        		"<aff> <institution>Department of Pathology,University of Cincinnati College of " +
        		"Medicine</institution>, <country>USA</country></aff>"
        };
        
		List<DocumentAffiliation> affs = instance.extractStrings(source);
		assertEquals(affs.size(), 2);
		for (int i = 0; i < 2; i++) {
			DocumentAffiliation aff = affs.get(i);
			Element affEl = new Element("aff");
			TokenizedTextToNLMExporter.addText(affEl, aff.getRawText(), aff.getTokens());
			String actual = outputter.outputString(affEl);
			String expected = expectedString[i];
			assertEquals(expected, actual);
		}
	}
}
