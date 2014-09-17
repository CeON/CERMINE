package pl.edu.icm.cermine.parsing.model;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationToken;

public class TokenTest {

	@Test
	public void testSequenceEquals() {
		List<AffiliationToken> sequence1 = Arrays.asList(
				new AffiliationToken("Bob", 1, 23),
				new AffiliationToken("Hop", 1, 23)
				);
		List<AffiliationToken> sequence2 = Arrays.asList(
				new AffiliationToken("bob", 19, 23),
				new AffiliationToken("hoP", 11, 87)
				);
		List<AffiliationToken> sequence3 = Arrays.asList(
				new AffiliationToken("Bobb", 1, 23),
				new AffiliationToken("Hop", 1, 23)
				);
		List<AffiliationToken> sequence4 = Arrays.asList(
				new AffiliationToken("Bob", 19, 23)
				);
		assertEquals(true, Token.sequenceTextEquals(sequence1, sequence2, false));
		assertEquals(false, Token.sequenceTextEquals(sequence1, sequence2, true));
		assertEquals(false, Token.sequenceTextEquals(sequence1, sequence3, false));
		assertEquals(false, Token.sequenceTextEquals(sequence1, sequence4, false));
	}

}
