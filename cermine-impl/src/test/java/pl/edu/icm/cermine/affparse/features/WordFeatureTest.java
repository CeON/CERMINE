package pl.edu.icm.cermine.affparse.features;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class WordFeatureTest {

	@Test
	public void testComputeFeature() {
		String word="BabaMaKota";
		String notWord = "123";
		WordFeature instance = new WordFeature(Arrays.asList((Feature)new IsNumber()));
		
		assertEquals("W=babamakota", instance.computeFeature(word));
		assertEquals(null, instance.computeFeature(notWord));
	}

}
