package pl.edu.icm.cermine.affparse.dictfeatures;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import pl.edu.icm.cermine.affparse.dictfeatures.DictionaryFeature;
import pl.edu.icm.cermine.affparse.model.AffiliationToken;
import pl.edu.icm.cermine.affparse.tools.AffiliationNormalizer;
import pl.edu.icm.cermine.affparse.tools.AffiliationTokenizer;

public class DictionaryFeatureTest {

	public class MockDictionaryFeature extends DictionaryFeature {

		public MockDictionaryFeature(boolean useLowerCase) {
			super(useLowerCase);
		}

		@Override
		protected String getFeatureString() {
			return "HIT";
		}

		@Override
		protected String getDictionaryFileName() {
			return "mock-dictionary.txt";
		}
		/*
			Elo Melo 320
			Hejka ziomeczku, :-P
			W chrząszczu Szczebrzeszyn.
		 */
		
	}
	
	@Test
	public void testAddFeatures() {
		MockDictionaryFeature instance = new MockDictionaryFeature(false);
		String text = "elo meLo  320, Elo Melo 32.0. Hejka ziomeczku,:-P. W W chrzaszczu szczebrzeszyn ..";
		//            "n...n.....n..n.n...n....n.nnn.y.....y........yyyyn.n.n.n..........n.............nn"
		int expectFeature[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
		List<AffiliationToken> tokens = AffiliationTokenizer.tokenize(
				AffiliationNormalizer.normalize(text));
		assertEquals(expectFeature.length, tokens.size());
		instance.addFeatures(tokens);
		
		for (int i = 0; i < expectFeature.length; i++) {
			assertEquals("Token: " + i + " " + tokens.get(i).getText(), expectFeature[i],
					tokens.get(i).getFeatures().size());
		}
		
		instance = new MockDictionaryFeature(true);
		text = "elo meLo  320, Elo Melo 32.0. Hejka ziómeczku,:-P. W W chrzaszczu szczebrzeszyn ..";
		//     "y...y.....y..n.n...n....n.nnn.y.....y........yyyyn.n.y.y..........y.............yn"
		int expectFeature2[] = {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0};
		tokens = AffiliationTokenizer.tokenize(
				AffiliationNormalizer.normalize(text));
		assertEquals(expectFeature2.length, tokens.size());
		instance.addFeatures(tokens);
		
		for (int i = 0; i < expectFeature2.length; i++) {
			assertEquals("Token: " + i + " " + tokens.get(i).getText(), expectFeature2[i],
					tokens.get(i).getFeatures().size());
		}
	}

}
