package pl.edu.icm.cermine.metadata.affiliation.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.AffiliationToken;
import pl.edu.icm.cermine.parsing.tools.TextTokenizer;

/**
 * Text tokenizer suitable for tokenizing affiliation strings.
 * 
 * @author Bartosz Tarnawski
 */
public class AffiliationTokenizer implements TextTokenizer<AffiliationToken> {
	
	private static List<AffiliationToken> asciiTextToTokens(String text,
			List<Integer> asciiIndices) {
		final String DELIMITER_REGEX = "\\d+|\\W|_";
		List<AffiliationToken>  tokens = new ArrayList<AffiliationToken>();
		Matcher delimeterMatcher = Pattern.compile(DELIMITER_REGEX).matcher(text);
		int lastEnd = 0;
		
		while (delimeterMatcher.find()) {
			int currentStart = delimeterMatcher.start();
			int currentEnd = delimeterMatcher.end();
		
			// skippedText may contain only letters, it may be an empty string
			String skippedText = text.substring(lastEnd, currentStart);
			if (!skippedText.equals("")) {
				tokens.add(new AffiliationToken(skippedText, 
						asciiIndices.get(lastEnd), asciiIndices.get(currentStart)));
			}

			// matched text may be a sequence of digits or a single non-alphanumeric character
			String matchedText = text.substring(currentStart, currentEnd);
			// ignore whitespace
			if (!matchedText.matches("\\s")) {
				tokens.add(new AffiliationToken(matchedText,
						asciiIndices.get(currentStart), asciiIndices.get(currentEnd)));
			}
			
			lastEnd = currentEnd;
		}
		
		String skippedText = text.substring(lastEnd, text.length());
		if (!skippedText.equals("")) {
			tokens.add(new AffiliationToken(skippedText, 
					asciiIndices.get(lastEnd), asciiIndices.get(text.length())));
		}
		
		return tokens;
	}
	
	/**
	 * Returns a list of indices of all ASCII characters in the text
	 * 
	 * @param text
	 * @return
	 */
	private static List<Integer> getAsciiSubstringIndices(String text) {
		List<Integer> indices = new ArrayList<Integer>();
		Matcher asciiMatcher = Pattern.compile("\\p{ASCII}").matcher(text);
		while (asciiMatcher.find()) {
			indices.add(asciiMatcher.start());
		}
		return indices;
	}
	

	/**
	 * Returns a string formed by the characters in the text with the given indices.
	 * 
	 * @param text
	 * @param indices
	 * @return
	 */
	private static String getSubstring(String text, List<Integer> indices) {
	    StringBuilder substringBuilder = new StringBuilder();
		for (int i : indices) {
			substringBuilder.append(text.charAt(i));
		}
		return substringBuilder.toString();
	}
	
	/**
	 * Tokenizes a normalized string. The string should be in NFKD, so that the accents
	 * are separated from the letters.
	 * 
	 * Non-ASCII characters are ignored. Words separated by spaces are treated as different tokens.
	 * Spaces are not kept as tokens. Sequences of digits are separated from sequences of letters.
	 * Non-alphanumeric characters are all treated as separate tokens.
	 * 
	 * @param text the text to tokenize, should be in NFKD
	 * @return list of tokens
	 */
	@Override
	public List<AffiliationToken> tokenize(String text) {
		List<Integer> asciiIndices = getAsciiSubstringIndices(text);
		String asciiText = getSubstring(text, asciiIndices);
		asciiIndices.add(text.length()); // Guardian index
		return asciiTextToTokens(asciiText, asciiIndices);
	}
}
