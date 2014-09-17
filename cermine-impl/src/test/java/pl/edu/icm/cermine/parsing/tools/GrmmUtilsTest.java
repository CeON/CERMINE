package pl.edu.icm.cermine.parsing.tools;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.parsing.model.Token;

public class GrmmUtilsTest {

	@Test
	public void testToGrmmInput1() {
		
		String expected = "TEXT ---- F1 F2 F3";
		String actual = GrmmUtils.toGrmmInput("TEXT", Arrays.asList("F1", "F2", "F3"));
		assertEquals(expected, actual);
	}

	@Test
	public void testToGrmmInput2() {
		Token<AffiliationLabel> token1 = new Token<AffiliationLabel>("sometext");
        token1.setLabel(AffiliationLabel.TEXT);
		token1.getFeatures().add("F1");
		token1.getFeatures().add("F2");

		Token<AffiliationLabel> token2 = new Token<AffiliationLabel>("sometext2");
        token2.setLabel(AffiliationLabel.TEXT);
		token2.getFeatures().add("F3");
		
		String expected = "TEXT ---- F1 F2 Start@-1 F3@1\n" +
				"TEXT ---- F3 F1@-1 F2@-1 End@1\n";
		String actual = GrmmUtils.toGrmmInput(Arrays.asList(token1, token2), 1);
		assertEquals(expected, actual);
	}
}
