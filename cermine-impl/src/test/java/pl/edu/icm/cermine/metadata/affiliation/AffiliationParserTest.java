package pl.edu.icm.cermine.metadata.affiliation;

import org.jdom.output.XMLOutputter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

public class AffiliationParserTest {

	@Test
	public void testParseString() throws AnalysisException, TransformationException {
		CRFAffiliationParser parser = new CRFAffiliationParser();
		XMLOutputter outputter = new XMLOutputter();
		String input = "Department of Dinozauring, Dino Institute, Tyranosaurus Route 35, Boston, MA, USA";
		String expected = "<aff id=\"id\"><label>id</label>" +
				"<institution>Department of Dinozauring, Dino Institute</institution>" +
				", " +
				"<addr-line>Tyranosaurus Route 35, Boston, MA</addr-line>" +
				", " +
				"<country country=\"US\">USA</country>" +
				"</aff>";
		String actual = outputter.outputString(parser.parse(input));
		assertEquals(expected, actual);
	}

	@Test
	public void testParseStringWithAuthor() throws AnalysisException, TransformationException {
		CRFAffiliationParser parser = new CRFAffiliationParser(
				"common-words-affiliations-with-author.txt",
				"acrf-affiliations-with-author.ser.gz");
		XMLOutputter outputter = new XMLOutputter();
		String input = "Andrew McDino and Elizabeth Pterodactyl, Department of Dinozauring, Dino Institute, Tyranosaurus Route 35, Boston, MA, USA";
		String expected = "<aff id=\"id\"><label>id</label>" +
//				"<author>Andrew McDino and Elizabeth Pterodactyl</author>" +
				", " +
				"<institution>Department of Dinozauring, Dino Institute</institution>" +
				", " +
				"<addr-line>Tyranosaurus Route 35, Boston, MA</addr-line>" +
				", " +
				"<country country=\"US\">USA</country>" +
				"</aff>";
		String actual = outputter.outputString(parser.parse(input));
		assertEquals(expected, actual);
	}

}
