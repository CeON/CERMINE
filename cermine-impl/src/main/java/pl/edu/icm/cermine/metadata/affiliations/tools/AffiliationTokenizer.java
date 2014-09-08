package pl.edu.icm.cermine.metadata.affiliations.tools;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.parsing.tools.TextTokenizer;

/**
 * Text tokenizer suitable for tokenizing affiliation strings.
 * 
 * @author Bartosz Tarnawski
 */
public class AffiliationTokenizer extends TextTokenizer<AffiliationToken> {
	
	private static List<AffiliationToken> asciiTextToTokens(String text,
			List<Integer> asciiIndices) {
		final String DELIMITER_REGEX = "\\d+|\\W|_";
		List<AffiliationToken>  tokens = new ArrayList<AffiliationToken>();
		Matcher delimeterMatcher = Pattern.compile(DELIMITER_REGEX).matcher(text);
		int lastEnd = 0;
		
		while (delimeterMatcher.find()) {
			int currentStart = delimeterMatcher.start();
			int currentEnd = delimeterMatcher.end();
			
			String skippedText = text.substring(lastEnd, currentStart);
			if (!skippedText.equals("")) {
				tokens.add(new AffiliationToken(skippedText, 
						asciiIndices.get(lastEnd), asciiIndices.get(currentStart)));
			}

			String matchedText = text.substring(currentStart, currentEnd);
			if (!matchedText.equals(" ")) {
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
	
	private static List<Integer> getAsciiSubstringIndices(String text) {
		List<Integer> indices = new ArrayList<Integer>();
		Matcher asciiMatcher = Pattern.compile("\\p{ASCII}").matcher(text);
		while (asciiMatcher.find()) {
			indices.add(asciiMatcher.start());
		}
		return indices;
	}
	
	private static String getSubstring(String text, List<Integer> indices) {
	    StringBuilder substringBuilder = new StringBuilder();
		for (int i : indices) {
			substringBuilder.append(text.charAt(i));
		}
		return substringBuilder.toString();
	}
	
	@Override
	public List<AffiliationToken> tokenize(String text) {
		List<Integer> asciiIndices = getAsciiSubstringIndices(text);
		String asciiText = getSubstring(text, asciiIndices);
		asciiIndices.add(text.length()); // Guardian index
		return asciiTextToTokens(asciiText, asciiIndices);
	}
}
