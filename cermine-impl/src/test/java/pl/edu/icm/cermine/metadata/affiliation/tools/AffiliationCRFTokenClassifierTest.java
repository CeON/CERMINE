package pl.edu.icm.cermine.metadata.affiliation.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.transformers.DocumentMetadataToNLMElementConverter;
import pl.edu.icm.cermine.parsing.model.Token;

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
		List<Token<AffiliationLabel>> tokens = new ArrayList<Token<AffiliationLabel>>();
		for (int i = 0; i < 5; i++) {
			tokens.add(new Token<AffiliationLabel>());
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
		
		for (Token<AffiliationLabel> token : tokens) {
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
        DocumentMetadataToNLMElementConverter converter = new DocumentMetadataToNLMElementConverter();
        Element element = converter.convertAffiliation(instance);
        XMLOutputter outputter = new XMLOutputter();
		String actual =  outputter.outputString(element);
		
		String expected =
				"<aff id=\"id\"><label>id</label><institution>Department</institution><institution>of</institution><institution>Oncology</institution><institution>,</institution><institution>Radiology</institution><institution>and</institution><institution>Clinical</institution><institution>Immunology</institution><institution>,</institution><institution>Akademiska</institution><institution>Sjukhuset</institution>,<addr-line>Uppsala</addr-line>,<country>Sweden</country></aff>";
		assertEquals(expected, actual);
	}
}
