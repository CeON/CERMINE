package pl.edu.icm.cermine.affparse.tools;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import pl.edu.icm.cermine.affparse.model.AffiliationLabel;
import pl.edu.icm.cermine.affparse.model.AffiliationToken;
import pl.edu.icm.cermine.exception.AnalysisException;

public class AffiliationExporterTest {

	@Test
	public void testToNLM() throws AnalysisException {
		//                  0         2         3         4
		//                  0123456789012345678901234567890123456789
		String text = " Silesian University, Katowice, Poland ";
		List<AffiliationToken> tokens = Arrays.asList(
				new AffiliationToken("", 1, 9, AffiliationLabel.INSTITUTION),
				new AffiliationToken("", 10, 20, AffiliationLabel.INSTITUTION),
				new AffiliationToken("", 20, 21, AffiliationLabel.INSTITUTION),
				new AffiliationToken("", 22, 30, AffiliationLabel.ADDRESS),
				new AffiliationToken("", 30, 31, AffiliationLabel.ADDRESS),
				new AffiliationToken("", 32, 38, AffiliationLabel.COUNTRY)
				);
		Element aff = AffiliationExporter.toNLM(text, tokens);
		XMLOutputter outputter = new XMLOutputter();
		String actual = outputter.outputString(aff);
		String expected = "<aff> <institution>Silesian University,</institution> <addr-line>Katowice,</addr-line> <country>Poland</country> </aff>";
		assertEquals(expected, actual);
	}

}
