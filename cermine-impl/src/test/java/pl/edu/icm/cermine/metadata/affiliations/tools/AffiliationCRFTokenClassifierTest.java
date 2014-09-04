package pl.edu.icm.cermine.metadata.affiliations.tools;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationCRFTokenClassifier;

public class AffiliationCRFTokenClassifierTest {

	@Test
	public void testClassify() throws AnalysisException {
		List<AffiliationToken> tokens = new ArrayList<AffiliationToken>();
		for (int i = 0; i < 5; i++) {
			tokens.add(new AffiliationToken());
		}
		tokens.get(0).setFeatures(Arrays.asList(
				"W=University",
				"Capital"
				));
		tokens.get(1).setFeatures(Arrays.asList(
				"W=,",
				"Punct"
				));
		tokens.get(2).setFeatures(Arrays.asList(
				"W=Boston",
				"Capital",
				"City"
				));
		tokens.get(3).setFeatures(Arrays.asList(
				"W=,",
				"Punct"
				));
		tokens.get(4).setFeatures(Arrays.asList(
				"W=USA",
				"AllCapital",
				"Country"
				));
		AffiliationCRFTokenClassifier.getInstance().classify(tokens);
		
		for (AffiliationToken token : tokens) {
			assertNotNull(token.getLabel());
			// System.out.println(token.getLabel().getTag());
		}
	}

}
