package pl.edu.icm.cermine.affparse.tools;

import static org.junit.Assert.*;

import org.junit.Test;

public class AffiliationNormalizerTest {

	@Test
	public void testNormalize() {
		char ci = 0x0107; // LATIN SMALL LETTER C WITH ACUTE
		char c = 0x63; // LATIN SMALL LETTER C
		char acute = 0x301; // COMBINING ACUTE ACCENT
		String input = Character.toString(ci);
		String expected = Character.toString(c) + Character.toString(acute);
		String actual = new AffiliationNormalizer().normalize(input);
		assertEquals(expected, actual);
		
		char dash = 0x2014; // EM_DASH
		input = Character.toString(dash);
		expected = " ";
		actual = new AffiliationNormalizer().normalize(input);
		assertEquals(expected, actual);
	}

}
