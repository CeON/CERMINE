package pl.edu.icm.cermine.metadata.affiliations.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationCRFTokenClassifier;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationFeatureExtractor;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.tools.ParsableStringParser;
import pl.edu.icm.cermine.parsing.tools.ParsableStringToNLMExporter;

/**
 * Affiliation parser. Processes an instance of DocumentAffiliation by
 * generating and tagging its tokens.
 * 
 * @author Bartosz Tarnawski
 */
public class AffiliationParser extends ParsableStringParser<DocumentAffiliation> {

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
	 * Sets the token list of the affiliation so that their labels
	 * determine the tagging of its text content. 
	 * 
	 * @param affiliation
	 * @throws AnalysisException
	 */
	public void parse(DocumentAffiliation affiliation) throws AnalysisException {
		affiliation.setTokens(tokenizer.tokenize(affiliation.getRawText()));
		featureExtractor.calculateFeatures(affiliation);
		classifier.classify(affiliation.getTokens());
	}

	/**
	 * @param affiliationString string representation of the affiliation to parse
	 * @return XML Element with the tagged affiliation in NLM format
	 * @throws AnalysisException
	 * @throws TransformationException 
	 */
	public Element parseString(String affiliationString) throws AnalysisException,
	TransformationException {
		DocumentAffiliation aff = new DocumentAffiliation(affiliationString);
		parse(aff);
		Element affElement = new Element("aff");
		ParsableStringToNLMExporter.addText(affElement, aff.getRawText(), aff.getTokens());
		return affElement;
	}
	
	// For testing purposes only
	public static void main(String[] args) throws IOException, AnalysisException,
	TransformationException {
		AffiliationParser parser = new AffiliationParser();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String affiliationText = br.readLine();
			DocumentAffiliation affiliation = new DocumentAffiliation(affiliationText);
			parser.parse(affiliation);
			System.out.println(affiliation.toXMLString());
		}
	}
}
