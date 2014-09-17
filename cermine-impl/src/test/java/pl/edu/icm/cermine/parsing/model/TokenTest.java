package pl.edu.icm.cermine.parsing.model;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;

public class TokenTest {

	@Test
	public void testSequenceEquals() {
		List<Token<AffiliationLabel>> sequence1 = Arrays.asList(
				new Token<AffiliationLabel>("Bob", 1, 23),
				new Token<AffiliationLabel>("Hop", 1, 23)
				);
		List<Token<AffiliationLabel>> sequence2 = Arrays.asList(
				new Token<AffiliationLabel>("bob", 19, 23),
				new Token<AffiliationLabel>("hoP", 11, 87)
				);
		List<Token<AffiliationLabel>> sequence3 = Arrays.asList(
				new Token<AffiliationLabel>("Bobb", 1, 23),
				new Token<AffiliationLabel>("Hop", 1, 23)
				);
		List<Token<AffiliationLabel>> sequence4 = Arrays.asList(
				new Token<AffiliationLabel>("Bob", 19, 23)
				);
		assertTrue(Token.sequenceTextEquals(sequence1, sequence2, false));
		assertFalse(Token.sequenceTextEquals(sequence1, sequence2, true));
		assertFalse(Token.sequenceTextEquals(sequence1, sequence3, false));
		assertFalse(Token.sequenceTextEquals(sequence1, sequence4, false));
	}

}
