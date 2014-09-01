package pl.edu.icm.cermine.affparse.dictfeatures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.edu.icm.cermine.affparse.model.AffiliationToken; // Zaleznosc od Affiliation! FIXME
import pl.edu.icm.cermine.affparse.model.Token;
import pl.edu.icm.cermine.affparse.tools.AffiliationNormalizer;
import pl.edu.icm.cermine.affparse.tools.AffiliationTokenizer;

public abstract class DictionaryFeature {

	private List<List<AffiliationToken>> entries;
	private Map<String, List<Integer>> dictionary;

	public DictionaryFeature() {
		entries = new ArrayList<List<AffiliationToken>>();
		dictionary = new HashMap<String, List<Integer>>();
		loadDictionary();
	}
	
	private void addLine(String line) {
		String normalizedLine = AffiliationNormalizer.normalize(line);
		
		List<AffiliationToken> tokens = AffiliationTokenizer
				.tokenize(normalizedLine);
		if (tokens.isEmpty()) {
			System.err.println("Line with no ASCII characters: " + line);
			return;
		}
		
		entries.add(tokens);
		int entryId = entries.size() - 1;
		String tokenString = tokens.get(0).getText().toLowerCase();
		
		if (!dictionary.containsKey(tokenString)) {
			dictionary.put(tokenString, new ArrayList<Integer>());
		}
		dictionary.get(tokenString).add(entryId);
	}

	private void loadDictionary() {
		String dictionaryFileName = getDictionaryFileName();
		InputStream is = getClass().getResourceAsStream(dictionaryFileName);
		if (is == null) {
			System.err.println("Resource not found: " + dictionaryFileName);
			return;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = in.readLine()) != null) {
				addLine(line);
			}
		} catch (IOException readException) {
			System.err.println("An exception occured when the dictionary "
					+ dictionaryFileName + " was being read: " + readException);
		} finally {
			try {
				is.close();
			} catch (IOException closeException) {
				System.err
						.println("An exception occured when the stream was being closed: "
								+ closeException);
			}
		}
	}

	public void addFeatures(List<AffiliationToken> tokens) {
		Set<Integer> candidateEntries = new HashSet<Integer>();
		for (AffiliationToken token : tokens) {
			List<Integer> candidatesForToken = dictionary.get(token.getText().toLowerCase());
			if (candidatesForToken != null) {
				candidateEntries.addAll(candidatesForToken);
			}
		}
		
		boolean marked[] = new boolean[tokens.size()];
		for (int i = 0; i < marked.length; i++) {
			marked[i] = false;
		}
		
		for (int entryId : candidateEntries) {
			List<AffiliationToken> entry = entries.get(entryId);
			for (int l = 0, r = entry.size(); r <= tokens.size(); l++, r++) {
				if (Token.sequenceEquals(entry, tokens.subList(l, r))) {
					for (int i = l; i < r; i++) {
						marked[i] = true;
					}
				}
			}
		}
		
		for (int i = 0; i < marked.length; i++) {
			if (marked[i]) {
				tokens.get(i).addFeature(getFeatureString());
			}
		}
	}
	
	protected abstract String getFeatureString();

	protected abstract String getDictionaryFileName();
}
