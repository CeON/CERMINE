package pl.edu.icm.cermine.metadata.affiliation.tools;

import pl.edu.icm.cermine.metadata.affiliation.tools.NLMAffiliationExtractor;
import java.io.IOException;
import java.util.List;
import org.jdom.JDOMException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.xml.sax.InputSource;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;

public class NLMAffiliationExtractorTest {

	private NLMAffiliationExtractor instance = new NLMAffiliationExtractor();
	
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
			String actual = affs.get(i).toXMLString();
			String expected = expectedString[i];
			assertEquals(expected, actual);
		}
	}
}
