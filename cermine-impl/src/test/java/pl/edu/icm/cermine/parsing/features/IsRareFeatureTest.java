package pl.edu.icm.cermine.parsing.features;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;

public class IsRareFeatureTest {

	private static final List<String> commonWords = Arrays.asList("pies", "kot", "kot1");
	private static final IsRareFeature featureCaseSensitive = new IsRareFeature(commonWords, true);
	private static final IsRareFeature featureIgnoreCase = new IsRareFeature(commonWords, false);
	
	@Test
	public void testCalculateFeaturePredicate() {
		AffiliationToken inSet = new AffiliationToken("kot");
		AffiliationToken lowerInSet = new AffiliationToken("KOT");
		AffiliationToken notInSet = new AffiliationToken("SZCZUR");
		AffiliationToken notWord = new AffiliationToken("kot1");
		DocumentAffiliation aff = new DocumentAffiliation("");
		assertEquals(false, featureCaseSensitive.calculateFeaturePredicate(inSet, aff));
		assertEquals(true, featureCaseSensitive.calculateFeaturePredicate(lowerInSet, aff));
		assertEquals(true, featureCaseSensitive.calculateFeaturePredicate(notInSet, aff));
		assertEquals(false, featureCaseSensitive.calculateFeaturePredicate(notWord, aff));
		
		assertEquals(false, featureIgnoreCase.calculateFeaturePredicate(inSet, aff));
		assertEquals(false, featureIgnoreCase.calculateFeaturePredicate(lowerInSet, aff));
		assertEquals(true, featureIgnoreCase.calculateFeaturePredicate(notInSet, aff));
		assertEquals(false, featureIgnoreCase.calculateFeaturePredicate(notWord, aff));
	}

}