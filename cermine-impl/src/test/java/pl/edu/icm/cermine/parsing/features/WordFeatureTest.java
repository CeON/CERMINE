package pl.edu.icm.cermine.parsing.features;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import pl.edu.icm.cermine.parsing.features.IsNumber;
import pl.edu.icm.cermine.parsing.features.LocalFeature;
import pl.edu.icm.cermine.parsing.features.WordFeature;

public class WordFeatureTest {

	@Test
	public void testComputeFeature() {
		String word="BabaMaKota";
		String notWord = "123";
		WordFeature instance = new WordFeature(Arrays.asList((LocalFeature)new IsNumber()), true);
		
		assertEquals("W=babamakota", instance.computeFeature(word));
		assertEquals(null, instance.computeFeature(notWord));
		
		instance = new WordFeature(Arrays.asList((LocalFeature)new IsNumber()), false);
		assertEquals("W=BabaMaKota", instance.computeFeature(word));
		assertEquals(null, instance.computeFeature(notWord));	
	}

}
