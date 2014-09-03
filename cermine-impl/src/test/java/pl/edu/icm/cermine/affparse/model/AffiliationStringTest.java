package pl.edu.icm.cermine.affparse.model;

import static org.junit.Assert.*;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import pl.edu.icm.cermine.exception.AnalysisException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	public void testCalculateFeatures() {
		String text = "Cóż ro123bić?";
	    List<List<String>> expectedFeatures = new ArrayList<List<String>>();
	    expectedFeatures.add(Arrays.asList("W=Coz", "UpperCase"));
	    expectedFeatures.add(Arrays.asList("W=ro"));
	    expectedFeatures.add(Arrays.asList("Number"));
	    expectedFeatures.add(Arrays.asList("W=bic"));
	    expectedFeatures.add(Arrays.asList("W=?", "WeirdLetter"));
		
	    AffiliationString instance = new AffiliationString(text);
		instance.calculateFeatures();
		for (int i = 0; i < expectedFeatures.size(); i++) {
			List<String> expected = expectedFeatures.get(i);
			List<String> actual = instance.getTokens().get(i).getFeatures();
			Collections.sort(expected);
			Collections.sort(actual);
			assertEquals(expected, actual);
		}
	}
	
	@Test
	public void testClassify() throws AnalysisException {
		String text = "Department of Oncology, Radiology and Clinical Immunology, Akademiska " +
				"Sjukhuset, Uppsala, Sweden";
		AffiliationString instance = new AffiliationString(text);
		instance.calculateFeatures();
		instance.classify();
		Element aff = instance.toNLM();
		XMLOutputter outputter = new XMLOutputter();
		String actual = outputter.outputString(aff);
		String expected =
				"<aff><institution>Department of Oncology, Radiology and Clinical Immunology, " +
				"Akademiska Sjukhuset</institution>, " + 
				"<addr-line>Uppsala</addr-line>, " +
				"<country>Sweden</country></aff>";
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToNLM() throws AnalysisException {
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
		String expected = "<aff> <institution>Uniwersytet Śląski</institution>, <addr-line>Katowice</addr-line> </aff>";
		assertEquals(expected, actual);
		
	}

}
