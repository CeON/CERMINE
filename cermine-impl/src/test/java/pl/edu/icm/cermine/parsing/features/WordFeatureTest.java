package pl.edu.icm.cermine.parsing.features;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;

public class WordFeatureTest {

	@Test
	public void testComputeFeature() {
		String word="BabaMaKota";
		String notWord = "123";
		
		List<AffiliationToken> tokens = Arrays.asList(
				new AffiliationToken(word),
				new AffiliationToken(notWord)
				);
		
		WordFeatureCalculator instance = new WordFeatureCalculator(
				Arrays.<BinaryTokenFeatureCalculator>asList(new IsNumberFeature()), true);
		
	    DocumentAffiliation aff = new DocumentAffiliation("");
		
		assertEquals("W=babamakota", instance.calculateFeatureValue(tokens.get(0), aff));
		assertEquals(null, instance.calculateFeatureValue(tokens.get(1), aff));
		
		instance = new WordFeatureCalculator(
				Arrays.<BinaryTokenFeatureCalculator>asList(new IsNumberFeature()), false);
		assertEquals("W=BabaMaKota", instance.calculateFeatureValue(tokens.get(0), aff));
		assertEquals(null, instance.calculateFeatureValue(tokens.get(1), aff));
	}

}
