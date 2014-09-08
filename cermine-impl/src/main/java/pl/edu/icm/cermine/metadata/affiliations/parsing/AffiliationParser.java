package pl.edu.icm.cermine.metadata.affiliations.parsing;

import java.util.List;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationCRFTokenClassifier;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationFeatureExtractor;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;

public class AffiliationParser {

	private AffiliationTokenizer tokenizer = null;
	private AffiliationFeatureExtractor featureExtractor = null;
	private AffiliationCRFTokenClassifier classifier = null;

	public AffiliationParser() throws AnalysisException {
		tokenizer = new AffiliationTokenizer();
		featureExtractor = new AffiliationFeatureExtractor();
		classifier = new AffiliationCRFTokenClassifier();
	}

	public void parseAffiliation(DocumentAffiliation affiliation) throws AnalysisException {
		affiliation.setTokens(tokenizer.tokenize(affiliation.getRawText()));
		featureExtractor.calculateFeatures(affiliation);
		classifier.classify(affiliation.getTokens());
	}

	public void parseAffiliation(List<DocumentAffiliation> affiliations) throws AnalysisException {
		for (DocumentAffiliation aff : affiliations) {
			parseAffiliation(aff);
		}
	}
}
