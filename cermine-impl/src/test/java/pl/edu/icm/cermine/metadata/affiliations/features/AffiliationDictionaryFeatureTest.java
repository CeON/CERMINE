package pl.edu.icm.cermine.metadata.affiliations.features;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import pl.edu.icm.cermine.metadata.affiliations.features.AffiliationDictionaryFeature;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.tools.MetadataTools;

public class AffiliationDictionaryFeatureTest {

	private static final AffiliationDictionaryFeature featureCaseSensitive =
			new AffiliationDictionaryFeature("HIT", "mock-dictionary.txt", false);
	private static final AffiliationDictionaryFeature featureIgnoreCase =
			new AffiliationDictionaryFeature("HIT", "mock-dictionary.txt", true);
	
        /* mock-dictionary.txt content:
                Elo Melo 320
                Hejka ziomeczku, :-P
                W chrząszczu Szczebrzeszyn.
         */
	
	@Test
	public void testAddFeatures() {
		String text = "elo meLo  320, Elo Melo 32.0. Hejka ziomeczku,:-P. W W chrzaszczu szczebrzeszyn ..";
		//            "n...n.....n..n.n...n....n.nnn.y.....y........yyyyn.n.n.n..........n.............nn"
		int expectFeature[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
		List<AffiliationToken> tokens = 
				new AffiliationTokenizer().tokenize(MetadataTools.clean(text));
		assertEquals(expectFeature.length, tokens.size());
		featureCaseSensitive.calculateDictionaryFeatures(tokens);
		
		for (int i = 0; i < expectFeature.length; i++) {
			assertEquals("Token: " + i + " " + tokens.get(i).getText(), expectFeature[i],
					tokens.get(i).getFeatures().size());
		}
		
		text = "elo meLo  320, Elo Melo 32.0. Hejka ziómeczku,:-P. W W chrzaszczu szczebrzeszyn ..";
		//     "y...y.....y..n.n...n....n.nnn.y.....y........yyyyn.n.y.y..........y.............yn"
		int expectFeature2[] = {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0};
		tokens = new AffiliationTokenizer().tokenize(MetadataTools.clean(text));
		assertEquals(expectFeature2.length, tokens.size());
		featureIgnoreCase.calculateDictionaryFeatures(tokens);
		
		for (int i = 0; i < expectFeature2.length; i++) {
			assertEquals("Token: " + i + " " + tokens.get(i).getText(), expectFeature2[i],
					tokens.get(i).getFeatures().size());
		}
	}

}
