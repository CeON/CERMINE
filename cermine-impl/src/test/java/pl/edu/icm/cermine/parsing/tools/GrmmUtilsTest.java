package pl.edu.icm.cermine.parsing.tools;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.parsing.tools.GrmmUtils;

public class GrmmUtilsTest {

	@Test
	public void testToGrmmInput1() {
		
		String expected = "TEXT ---- F1 F2 F3";
		String actual = GrmmUtils.toGrmmInput("TEXT", Arrays.asList("F1", "F2", "F3"));
		assertEquals(expected, actual);
	}

	@Test
	public void testToGrmmInput2() {
		AffiliationToken token1 = new AffiliationToken("sometext");
		token1.getFeatures().add("F1");
		token1.getFeatures().add("F2");

		AffiliationToken token2 = new AffiliationToken("sometext2");
		token2.getFeatures().add("F3");
		
		String expected = "INST ---- F1 F2 Start@-1 F3@1\n" +
				"INST ---- F3 F1@-1 F2@-1 End@1\n";
		String actual = GrmmUtils.toGrmmInput(Arrays.asList(token1, token2), 1);
		assertEquals(expected, actual);
	}
}
