package pl.edu.icm.cermine.metadata.affiliations.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

	
	private List<String> loadWords(String wordsFileName) throws AnalysisException {
		List<String> commonWords = new ArrayList<String>();
		InputStream is = getClass().getResourceAsStream(wordsFileName);
		if (is == null) {
			throw new AnalysisException("Resource not found: " + wordsFileName);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = in.readLine()) != null) {
				commonWords.add(line);
			}
		} catch (IOException readException) {
			throw new AnalysisException("An exception occured when the common word list "
					+ wordsFileName + " was being read: " + readException);
		} finally {
			try {
				in.close();
			} catch (IOException closeException) {
				throw new AnalysisException("An exception occured when the stream was being " +
						"closed: " + closeException);
			}
		}
		return commonWords;
	}
	
	public AffiliationParser() throws AnalysisException {
		List<String> commonWords = loadWords("common-words.txt");
		tokenizer = new AffiliationTokenizer();
		featureExtractor = new AffiliationFeatureExtractor(commonWords);
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
