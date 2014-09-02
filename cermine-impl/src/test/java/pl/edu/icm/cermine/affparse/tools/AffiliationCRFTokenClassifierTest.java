package pl.edu.icm.cermine.affparse.tools;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import pl.edu.icm.cermine.affparse.model.AffiliationToken;
import pl.edu.icm.cermine.exception.AnalysisException;

public class AffiliationCRFTokenClassifierTest {

	@Test
	public void testClassify() throws AnalysisException {
		List<AffiliationToken> tokens = new ArrayList<AffiliationToken>();
		for (int i = 0; i < 5; i++) {
			tokens.add(new AffiliationToken());
		}
		tokens.get(0).setFeatures(Arrays.asList(
				"W=university",
				"Capital"
				));
		tokens.get(1).setFeatures(Arrays.asList(
				"W=,",
				"Punct"
				));
		tokens.get(2).setFeatures(Arrays.asList(
				"W=boston",
				"Capital",
				"City"
				));
		tokens.get(3).setFeatures(Arrays.asList(
				"W=,",
				"Punct"
				));
		tokens.get(4).setFeatures(Arrays.asList(
				"W=usa",
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
