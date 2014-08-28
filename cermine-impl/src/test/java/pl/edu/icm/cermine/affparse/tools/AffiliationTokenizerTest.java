package pl.edu.icm.cermine.affparse.tools;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import pl.edu.icm.cermine.affparse.model.AffiliationToken;

public class AffiliationTokenizerTest {

	@Test
	public void testTokenizeAscii() {
		//              012345678901234567
		String input = "ko  pi_es123_@@123Kot";
		// "ko", "pi" ,"_", "es", "123", "_", "@", "@", "123"
		
		List<AffiliationToken> expected = Arrays.asList(
				new AffiliationToken("ko", 0, 2),
				new AffiliationToken("pi", 4, 6),
				new AffiliationToken("_", 6, 7),
				new AffiliationToken("es", 7, 9),
				new AffiliationToken("123", 9, 12),
				new AffiliationToken("_", 12, 13),
				new AffiliationToken("@", 13, 14),
				new AffiliationToken("@", 14, 15),
				new AffiliationToken("123", 15, 18),
				new AffiliationToken("Kot", 18, 21)
				);
		
		List<AffiliationToken> actual = AffiliationTokenizer.tokenize(input);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testTokenizeNonAscii() {
		String input = "śćdź óó";
		input = AffiliationNormalizer.normalize(input);
		
		//          012345678901
		// input = "s'c'dz' o'o'"
		
		
		List<AffiliationToken> expected = Arrays.asList(
				new AffiliationToken("scdz", 0, 7),
				new AffiliationToken("oo", 8, 12)
				);
		
		List<AffiliationToken> actual = AffiliationTokenizer.tokenize(input);
		
		assertEquals(expected, actual);
	}

}
