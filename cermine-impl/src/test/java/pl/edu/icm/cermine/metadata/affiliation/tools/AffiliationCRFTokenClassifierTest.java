package pl.edu.icm.cermine.metadata.affiliation.tools;

import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationFeatureExtractor;
import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationCRFTokenClassifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;

public class AffiliationCRFTokenClassifierTest {

	private static final AffiliationTokenizer tokenizer = new AffiliationTokenizer();
	private static final AffiliationFeatureExtractor extractor;

	static {
		try {
			extractor = new AffiliationFeatureExtractor();
		} catch (AnalysisException e) {
			throw new RuntimeException("Failed to initialize the feature extractor");
		}
	}
	
	
	@Test
	public void testClassify() throws AnalysisException {
		List<AffiliationToken> tokens = new ArrayList<AffiliationToken>();
		for (int i = 0; i < 5; i++) {
			tokens.add(new AffiliationToken());
		}
		tokens.get(0).setFeatures(Arrays.asList(
				"W=University",
				"IsUpperCase"
				));
		tokens.get(1).setFeatures(Arrays.asList(
				"W=,",
				"IsSeparator"
				));
		tokens.get(2).setFeatures(Arrays.asList(
				"W=Boston",
				"IsUpperCase",
				"KeywordCity"
				));
		tokens.get(3).setFeatures(Arrays.asList(
				"W=,",
				"IsSeparator"
				));
		tokens.get(4).setFeatures(Arrays.asList(
				"W=USA",
				"IsAllCapital",
				"KeywordCountry"
				));
		new AffiliationCRFTokenClassifier().classify(tokens);
		
		for (AffiliationToken token : tokens) {
			assertNotNull(token.getLabel());
		}
	}

	
	@Test
	public void testClassifyWithDocumentAffiliation() throws AnalysisException, TransformationException {
		String text = "Department of Oncology, Radiology and Clinical Immunology, Akademiska " +
				"Sjukhuset, Uppsala, Sweden";
	    DocumentAffiliation instance = new DocumentAffiliation(text);
	    instance.setTokens(tokenizer.tokenize(instance.getRawText()));
	    extractor.calculateFeatures(instance);
		new AffiliationCRFTokenClassifier().classify(instance.getTokens());
		String actual = instance.toXMLString();
		String expected =
				"<aff><institution>Department of Oncology, Radiology and Clinical Immunology, " +
				"Akademiska Sjukhuset</institution>, " + 
				"<addr-line>Uppsala</addr-line>, " +
				"<country>Sweden</country></aff>";
		assertEquals(expected, actual);
	}
}
