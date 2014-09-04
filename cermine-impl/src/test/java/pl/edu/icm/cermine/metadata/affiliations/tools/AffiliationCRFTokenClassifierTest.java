package pl.edu.icm.cermine.metadata.affiliations.tools;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationCRFTokenClassifier;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.tools.TokenizedTextToNLMExporter;

public class AffiliationCRFTokenClassifierTest {

	private static final AffiliationTokenizer tokenizer = new AffiliationTokenizer();
	private static final AffiliationFeatureExtractor extractor = new AffiliationFeatureExtractor();
	
	
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
		new AffiliationCRFTokenClassifier().classify(tokens);
		
		for (AffiliationToken token : tokens) {
			assertNotNull(token.getLabel());
			// System.out.println(token.getLabel().getTag());
		}
	}

	
	@Test
	public void testClassifyWithDocumentAffiliation() throws AnalysisException, TransformationException {
		String text = "Department of Oncology, Radiology and Clinical Immunology, Akademiska " +
				"Sjukhuset, Uppsala, Sweden";
	    DocumentAffiliation instance = new DocumentAffiliation("someId", text);
	    instance.setTokens(tokenizer.tokenize(instance.getRawText()));
	    extractor.extractFeatures(instance.getTokens());
		new AffiliationCRFTokenClassifier().classify(instance.getTokens());
		Element aff = new Element("aff");
		TokenizedTextToNLMExporter.addText(aff, instance.getRawText(), instance.getTokens());
		
		XMLOutputter outputter = new XMLOutputter();
		String actual = outputter.outputString(aff);
		String expected =
				"<aff><institution>Department of Oncology, Radiology and Clinical Immunology, " +
				"Akademiska Sjukhuset</institution>, " + 
				"<addr-line>Uppsala</addr-line>, " +
				"<country>Sweden</country></aff>";
		assertEquals(expected, actual);
	}
}
