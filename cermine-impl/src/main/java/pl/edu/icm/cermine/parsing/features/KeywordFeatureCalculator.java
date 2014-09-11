package pl.edu.icm.cermine.parsing.features;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.tools.MetadataTools;
import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.tools.TextTokenizer;

/**
 * Class for calculating keyword features. It finds all occurrences of a keyword
 * string in a list of tokens and adds the appropriate feature string to the corresponding tokens
 * 
 * @author Bartosz Tarnawski
 * @param <T> type of tokens
 */
public class KeywordFeatureCalculator<T extends Token<?>> {

	private List<List<T>> entries;
	private Map<String, List<Integer>> dictionary;
	private TextTokenizer<T> textTokenizer;

	private String featureString;
	private boolean caseSensitive;

	/**
	 * @param FeatureString the string which will be added to the matching tokens' features lists
	 * @param dictionaryFileName the name of the dictionary to be used
	 * @param caseSensitive whether dictionary lookups should be case sensitive
	 * @param tokenizer used for dictionary entries splitting
	 */
	public KeywordFeatureCalculator(String FeatureString, String dictionaryFileName,
			boolean caseSensitive, TextTokenizer<T> tokenizer) throws AnalysisException {

		this.entries = new ArrayList<List<T>>();
		this.dictionary = new HashMap<String, List<Integer>>();
		this.textTokenizer = tokenizer;

		this.featureString = FeatureString;
		this.caseSensitive = caseSensitive;
		
		loadDictionary(dictionaryFileName);
	}
	
	private void addLine(String line, int number) {
		String normalizedLine = MetadataTools.clean(line);
		
		List<T> tokens = textTokenizer.tokenize(normalizedLine);
		if (tokens.isEmpty()) {
			System.err.println("Line (" + number + ") with no ASCII characters: " + line);
			return;
		}
		
		entries.add(tokens);
		int entryId = entries.size() - 1;
		String tokenString = tokens.get(0).getText();
		if (!caseSensitive) {
			tokenString = tokenString.toLowerCase();
		}
		
		if (!dictionary.containsKey(tokenString)) {
			dictionary.put(tokenString, new ArrayList<Integer>());
		}
		dictionary.get(tokenString).add(entryId);
	}

	private void loadDictionary(String dictionaryFileName) throws AnalysisException {
		InputStream is = getClass().getResourceAsStream(dictionaryFileName);
		if (is == null) {
			throw new AnalysisException("Resource not found: " + dictionaryFileName);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			int lineNumber = 1;
			while ((line = in.readLine()) != null) {
				addLine(line, lineNumber++);
			}
		} catch (IOException readException) {
			throw new AnalysisException("An exception occured when the dictionary "
					+ dictionaryFileName + " was being read: " + readException);
		} finally {
			try {
				in.close();
			} catch (IOException closeException) {
				throw new AnalysisException("An exception occured when the stream was being " +
						"closed: " + closeException);
			}
		}
	}

	/**
	 * Finds all occurrences of keywords from the dictionary in the text formed by the
	 * sequence of the tokens and marks the corresponding tokens by adding an appropriate string
	 * to their feature lists.
	 * 
	 * @param tokens
	 */
	public void calculateDictionaryFeatures(List<T> tokens) {
		
		boolean marked[] = new boolean[tokens.size()];
		for (int i = 0; i < marked.length; i++) {
			marked[i] = false;
		}
	
		for (int l = 0; l < tokens.size(); l++) {
			T token = tokens.get(l);
			String tokenString = token.getText();
			if (!caseSensitive) {
				tokenString = tokenString.toLowerCase();
			}
			List<Integer> candidateIds = dictionary.get(tokenString);
			if (candidateIds != null) {
				for (int candidateId : candidateIds) {
					List<T> entry = entries.get(candidateId);
					int r = l + entry.size();
					if (r <= tokens.size() && Token.sequenceTextEquals(entry, tokens.subList(l, r),
							caseSensitive)) {
						for (int i = l; i < r; i++) {
							marked[i] = true;
                    	}
					}
				}
			}
		}
		
		for (int i = 0; i < marked.length; i++) {
			if (marked[i]) {
				tokens.get(i).addFeature(featureString);
			}
		}
	}
}
