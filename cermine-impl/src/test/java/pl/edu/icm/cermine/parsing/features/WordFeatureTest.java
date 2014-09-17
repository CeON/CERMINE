package pl.edu.icm.cermine.parsing.features;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.model.Token;

public class WordFeatureTest {

	@Test
	public void testComputeFeature() {
		String word="BabaMaKota";
		String notWord = "123";
		
		List<Token<AffiliationLabel>> tokens = Arrays.asList(
				new Token<AffiliationLabel>(word),
				new Token<AffiliationLabel>(notWord)
				);
		
		WordFeatureCalculator instance = new WordFeatureCalculator(
				Arrays.<BinaryTokenFeatureCalculator>asList(new IsNumberFeature()), true);
		
	    DocumentAffiliation aff = new DocumentAffiliation("");
		
		assertEquals("W=babamakota", instance.calculateFeatureValue(tokens.get(0), aff));
		assertNull(instance.calculateFeatureValue(tokens.get(1), aff));
		
		instance = new WordFeatureCalculator(
				Arrays.<BinaryTokenFeatureCalculator>asList(new IsNumberFeature()), false);
		assertEquals("W=BabaMaKota", instance.calculateFeatureValue(tokens.get(0), aff));
		assertNull(instance.calculateFeatureValue(tokens.get(1), aff));
	}

}
