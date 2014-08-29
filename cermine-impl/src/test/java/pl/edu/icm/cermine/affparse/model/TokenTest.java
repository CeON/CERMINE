package pl.edu.icm.cermine.affparse.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

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
		assertEquals(true, Token.sequenceEquals(sequence1, sequence2));
		assertEquals(false, Token.sequenceEquals(sequence1, sequence3));
		assertEquals(false, Token.sequenceEquals(sequence1, sequence4));
	}

}
