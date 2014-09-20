package pl.edu.icm.cermine.metadata.tools;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MetadataToolsTest {
	
	@Test
	public void testClean() {
		char ci = 0x0107; // LATIN SMALL LETTER C WITH ACUTE
		char c = 0x63; // LATIN SMALL LETTER C
		char acute = 0x301; // COMBINING ACUTE ACCENT
		String input = Character.toString(ci);
		String expected = Character.toString(c) + Character.toString(acute);
		String actual = MetadataTools.cleanAndNormalize(input);
		assertEquals(expected, actual);
		
		char dash = 0x2014; // EM_DASH
		input = Character.toString(dash);
		expected = "-";
		actual = MetadataTools.cleanAndNormalize(input);
		assertEquals(expected, actual);
	}
}
