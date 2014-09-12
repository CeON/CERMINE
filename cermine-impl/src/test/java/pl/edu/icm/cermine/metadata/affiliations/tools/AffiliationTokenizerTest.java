package pl.edu.icm.cermine.metadata.affiliations.tools;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.tools.MetadataTools;

public class AffiliationTokenizerTest {
	
	private final static AffiliationTokenizer tokenizer = new AffiliationTokenizer();
	
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
		
		List<AffiliationToken> actual = tokenizer.tokenize(input);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testTokenizeNonAscii() {
		String input = "śćdź óó";
		input = MetadataTools.clean(input);
		//input = new AffiliationNormalizer().normalize(input);
		
		//          012345678901
		// input = "s'c'dz' o'o'"
		
		
		List<AffiliationToken> expected = Arrays.asList(
				new AffiliationToken("scdz", 0, 7),
				new AffiliationToken("oo", 8, 12)
				);
		
		List<AffiliationToken> actual = tokenizer.tokenize(input);
		
		assertEquals(expected, actual);
	}

	@Test
	public void testTokenizeWithDocumentAffiliation() {
		// 0123456789012345
		// Co'z' ro123bic'?
		String text = "Cóż ro123bić?";
		List<AffiliationToken> expected = Arrays.asList(
				new AffiliationToken("Coz", 0, 5),
				new AffiliationToken("ro", 6, 8),
				new AffiliationToken("123", 8, 11),
				new AffiliationToken("bic", 11, 15),
				new AffiliationToken("?", 15, 16)
				);
				
		DocumentAffiliation instance = new DocumentAffiliation(text);
		
		List<AffiliationToken> actual = tokenizer.tokenize(instance.getRawText());
		
		assertEquals(expected, actual);
	}

}
