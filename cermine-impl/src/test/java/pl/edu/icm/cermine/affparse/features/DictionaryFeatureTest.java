package pl.edu.icm.cermine.affparse.features;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import pl.edu.icm.cermine.affparse.model.AffiliationToken;
import pl.edu.icm.cermine.affparse.tools.AffiliationNormalizer;
import pl.edu.icm.cermine.affparse.tools.AffiliationTokenizer;

public class DictionaryFeatureTest {

	public class MockDictionaryFeature extends DictionaryFeature {

		@Override
		protected String getFeatureString() {
			return "HIT";
		}

		@Override
		protected String getDictionaryFileName() {
			return "mock-dictionary.txt";
		}
		
	}
	
	@Test
	public void testAddFeatures() {
		MockDictionaryFeature instance = new MockDictionaryFeature();
		String text = "elo meLo  320, Elo Melo 32.0. Hejka ziómeczku,:-P. W W chrzaszczu szczebrzeszyn ..";
		//            "y...y.....y..n.n...n....n.nnn.y.....y........yyyyn.n.y.y..........y.............yn"
		int expectFeature[] = {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0};
		List<AffiliationToken> tokens = AffiliationTokenizer.tokenize(
				AffiliationNormalizer.normalize(text));
		assertEquals(expectFeature.length, tokens.size());
		instance.addFeatures(tokens);
		
		for (int i = 0; i < expectFeature.length; i++) {
			assertEquals("Token: " + i + " " + tokens.get(i).getText(), expectFeature[i],
					tokens.get(i).getFeatures().size());
		}
		
	}

}
