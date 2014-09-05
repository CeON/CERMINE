package pl.edu.icm.cermine.parsing.features;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
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
		
	    DocumentAffiliation aff = new DocumentAffiliation("someId", "");
		
		assertEquals("W=babamakota", instance.calculateFeatureValue(tokens.get(0), aff));
		assertEquals(null, instance.calculateFeatureValue(tokens.get(1), aff));
		
		instance = new WordFeatureCalculator(
				Arrays.<BinaryTokenFeatureCalculator>asList(new IsNumberFeature()), false);
		assertEquals("W=BabaMaKota", instance.calculateFeatureValue(tokens.get(0), aff));
		assertEquals(null, instance.calculateFeatureValue(tokens.get(1), aff));
	}

}
