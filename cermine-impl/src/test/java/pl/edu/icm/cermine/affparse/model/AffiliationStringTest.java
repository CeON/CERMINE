package pl.edu.icm.cermine.affparse.model;

import static org.junit.Assert.*;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;


public class AffiliationStringTest {

	@Test
	public void testAffiliationString() {
		// 0123456789012345
		// Co'z' ro123bic'?
		String text = "Cóż ro123bić?";
		List<AffiliationToken> expectedTokens = Arrays.asList(
				new AffiliationToken("Coz", 0, 5),
				new AffiliationToken("ro", 6, 8),
				new AffiliationToken("123", 8, 11),
				new AffiliationToken("bic", 11, 15),
				new AffiliationToken("?", 15, 16)
				);
				
		AffiliationString instance = new AffiliationString(text);
		
		assertEquals(expectedTokens, instance.getTokens());
	}
	
	@Test
	public void testToNLM() {
		String text = " Uniwersytet Śląski, Katowice ";
		AffiliationString instance = new AffiliationString(text);
		List<AffiliationToken> tokens = instance.getTokens();
		int expectedSize = 4;
		assertEquals(expectedSize, tokens.size());
		tokens.get(0).setLabel(AffiliationLabel.INSTITUTION);
		tokens.get(1).setLabel(AffiliationLabel.INSTITUTION);
		tokens.get(2).setLabel(AffiliationLabel.INSTITUTION);
		tokens.get(3).setLabel(AffiliationLabel.ADDRESS);
		
		Element aff = instance.toNLM();
		XMLOutputter outputter = new XMLOutputter();
		String actual = outputter.outputString(aff);
		String expected = "<aff> <institution>Uniwersytet Śląski,</institution> <addr-line>Katowice</addr-line> </aff>";
		assertEquals(expected, actual);
		
	}

}
