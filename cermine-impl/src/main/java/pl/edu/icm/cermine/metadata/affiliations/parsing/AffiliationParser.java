package pl.edu.icm.cermine.metadata.affiliations.parsing;

import java.util.List;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationCRFTokenClassifier;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationFeatureExtractor;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;

/**
 * Affiliation parser.
 * 
 * @author Bartosz Tarnawski
 */
public class AffiliationParser {

	private AffiliationTokenizer tokenizer = null;
	private AffiliationFeatureExtractor featureExtractor = null;
	private AffiliationCRFTokenClassifier classifier = null;

	public AffiliationParser() throws AnalysisException {
		tokenizer = new AffiliationTokenizer();
		featureExtractor = new AffiliationFeatureExtractor();
		classifier = new AffiliationCRFTokenClassifier();
	}

	/**
	 * Parses an affiliation by setting predicted token labels.
	 * The affiliation instance is assumed to be tokenized, its tokens are assumed
	 * to hold appropriate lists of features.
	 * 
	 * @param affiliation
	 * @throws AnalysisException
	 */
	public void parseAffiliation(DocumentAffiliation affiliation) throws AnalysisException {
		affiliation.setTokens(tokenizer.tokenize(affiliation.getRawText()));
		featureExtractor.calculateFeatures(affiliation);
		classifier.classify(affiliation.getTokens());
	}

	/**
	 * Parses affiliations by setting predicted token labels.
	 * All affiliation instances are assumed to be tokenized, their tokens are assumed
	 * to hold appropriate lists of features.
	 * 
	 * @param affiliations
	 * @throws AnalysisException
	 */
	public void parseAffiliation(List<DocumentAffiliation> affiliations) throws AnalysisException {
		for (DocumentAffiliation aff : affiliations) {
			parseAffiliation(aff);
		}
	}
}
