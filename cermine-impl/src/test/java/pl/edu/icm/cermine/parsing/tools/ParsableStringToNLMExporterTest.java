package pl.edu.icm.cermine.parsing.tools;

import java.util.Arrays;
import java.util.List;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;

public class ParsableStringToNLMExporterTest {

	private static final AffiliationTokenizer tokenizer = new AffiliationTokenizer();
	
	@Test
	public void testAddText1() throws TransformationException {
		//                  0         2         3         4
		//                  0123456789012345678901234567890123456789
		String text = " Silesian University, Katowice, Poland ";
		List<AffiliationToken> tokens = Arrays.asList(
				new AffiliationToken("", 1, 9, AffiliationLabel.INSTITUTION),
				new AffiliationToken("", 10, 20, AffiliationLabel.INSTITUTION),
				new AffiliationToken("", 20, 21, AffiliationLabel.TEXT),
				new AffiliationToken("", 22, 30, AffiliationLabel.ADDRESS),
				new AffiliationToken("", 30, 31, AffiliationLabel.TEXT),
				new AffiliationToken("", 32, 38, AffiliationLabel.COUNTRY)
				);
		Element aff = new Element("aff");
		ParsableStringToNLMExporter.addText(aff, text, tokens);
		
		XMLOutputter outputter = new XMLOutputter();
		String actual = outputter.outputString(aff);
		String expected = "<aff> " +
				"<institution>Silesian University</institution>, " +
				"<addr-line>Katowice</addr-line>, " +
				"<country>Poland</country> </aff>";
		assertEquals(expected, actual);
	}

	@Test
	public void testAddTextWithDocumentAffiliation() throws TransformationException {
		String text = " Uniwersytet Śląski, Katowice ";
		DocumentAffiliation instance = new DocumentAffiliation(text);
		List<AffiliationToken> tokens = tokenizer.tokenize(instance.getRawText());
		int expectedSize = 4;
		assertEquals(expectedSize, tokens.size());
		tokens.get(0).setLabel(AffiliationLabel.INSTITUTION);
		tokens.get(1).setLabel(AffiliationLabel.INSTITUTION);
		tokens.get(2).setLabel(AffiliationLabel.TEXT);
		tokens.get(3).setLabel(AffiliationLabel.ADDRESS);
		instance.setTokens(tokens);
		
		Element aff = new Element("aff");
		ParsableStringToNLMExporter.addText(aff, instance.getRawText(), instance.getTokens());
		XMLOutputter outputter = new XMLOutputter();
		String actual = outputter.outputString(aff);
		String expected = "<aff> <institution>Uniwersytet Śląski</institution>, <addr-line>Katowice</addr-line> </aff>";
		assertEquals(expected, actual);	
	}
}
