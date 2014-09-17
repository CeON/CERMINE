package pl.edu.icm.cermine.metadata.affiliation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationCRFTokenClassifier;
import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationFeatureExtractor;
import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.tools.ParsableStringParser;
import pl.edu.icm.cermine.parsing.tools.ParsableStringToNLMExporter;

/**
 * Affiliation parser. Processes an instance of DocumentAffiliation by
 * generating and tagging its tokens.
 * 
 * @author Bartosz Tarnawski
 */
public class AffiliationParser implements ParsableStringParser<DocumentAffiliation> {

	private AffiliationTokenizer tokenizer = null;
	private AffiliationFeatureExtractor featureExtractor = null;
	private AffiliationCRFTokenClassifier classifier = null;
	
	private static final String DEFAULT_MODEL_FILE = 
			"/pl/edu/icm/cermine/metadata/affiliation/acrf-affiliations-pubmed.ser.gz";
	private static final String DEFAULT_COMMON_WORDS_FILE = 
			"/pl/edu/icm/cermine/metadata/affiliation/common-words-affiliations-pubmed.txt";

	
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
	 * @param acrfFileName the name of the package resource to be used as the ACRF model
	 * @throws AnalysisException
	 */
	public AffiliationParser(String wordsFileName, String acrfFileName) throws AnalysisException {
		List<String> commonWords = loadWords(wordsFileName);
		tokenizer = new AffiliationTokenizer();
		featureExtractor = new AffiliationFeatureExtractor(commonWords);
		classifier = new AffiliationCRFTokenClassifier(
				getClass().getResourceAsStream(acrfFileName));
	}
	
	public AffiliationParser() throws AnalysisException {
		this(DEFAULT_COMMON_WORDS_FILE, DEFAULT_MODEL_FILE);
	}

	/**
	 * Sets the token list of the affiliation so that their labels
	 * determine the tagging of its text content. 
	 * 
	 * @param affiliation
	 * @throws AnalysisException
	 */
    @Override
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
    @Override
	public Element parse(String affiliationString) throws AnalysisException,
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
		XMLOutputter outputter = new XMLOutputter();
		while (true) {
			String affiliationString = br.readLine();
			outputter.outputString(parser.parse(affiliationString));
		}
	}
}
