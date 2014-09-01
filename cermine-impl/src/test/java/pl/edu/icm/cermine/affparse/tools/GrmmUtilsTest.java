package pl.edu.icm.cermine.affparse.tools;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.edu.icm.cermine.affparse.model.AffiliationToken;

public class GrmmUtilsTest {

	@Test
	public void test() {
		AffiliationToken token = new AffiliationToken("sometext");
		token.getFeatures().add("FEATURE1");
		token.getFeatures().add("FEATURE2");
		token.getFeatures().add("FEATURE3");
		
		String expected = "TEXT ---- FEATURE1 FEATURE2 FEATURE3";
		String actual = GrmmUtils.toGrmmInput(token);
		
		assertEquals(expected, actual);
	}

}
