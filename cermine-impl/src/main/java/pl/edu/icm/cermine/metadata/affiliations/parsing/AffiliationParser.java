package pl.edu.icm.cermine.metadata.affiliations.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
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
	
	private static final String DEFAULT_COMMON_WORDS_FILE = "common-words-affiliations.txt";

	
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
	
	/**
	 * @param wordsFileName the name of the package resource to be used as the common words list
	 * @throws AnalysisException
	 */
	public AffiliationParser(String wordsFileName) throws AnalysisException {
		List<String> commonWords = loadWords(wordsFileName);
		tokenizer = new AffiliationTokenizer();
		featureExtractor = new AffiliationFeatureExtractor(commonWords);
		classifier = new AffiliationCRFTokenClassifier();
	}
	
	public AffiliationParser() throws AnalysisException {
		this(DEFAULT_COMMON_WORDS_FILE);
	}

	/**
	 * Parses an affiliation by setting predicted token labels.
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
	 * 
	 * @param affiliations
	 * @throws AnalysisException
	 */
	public void parseAffiliation(List<DocumentAffiliation> affiliations) throws AnalysisException {
		for (DocumentAffiliation aff : affiliations) {
			parseAffiliation(aff);
		}
	}
	
	// For testing purposes only
	public static void main(String[] args) throws IOException, AnalysisException,
	TransformationException {
		AffiliationParser parser = new AffiliationParser();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String affiliationText = br.readLine();
			DocumentAffiliation affiliation = new DocumentAffiliation(affiliationText);
			parser.parseAffiliation(affiliation);
			System.out.println(affiliation.toXMLString());
		}
	}
}
