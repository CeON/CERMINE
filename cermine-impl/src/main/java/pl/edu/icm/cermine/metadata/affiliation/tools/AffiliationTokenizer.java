/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */
package pl.edu.icm.cermine.metadata.affiliation.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.tools.TextTokenizer;

/**
 * Text tokenizer suitable for tokenizing affiliation strings.
 *
 * @author Bartosz Tarnawski
 */
public class AffiliationTokenizer implements TextTokenizer<Token<AffiliationLabel>> {

    private static List<Token<AffiliationLabel>> asciiTextToTokens(String text,
            List<Integer> asciiIndices) {
        final String DELIMITER_REGEX = "\\d+|\\W|_";
        List<Token<AffiliationLabel>> tokens = new ArrayList<Token<AffiliationLabel>>();
        Matcher delimeterMatcher = Pattern.compile(DELIMITER_REGEX).matcher(text);
        int lastEnd = 0;

        while (delimeterMatcher.find()) {
            int currentStart = delimeterMatcher.start();
            int currentEnd = delimeterMatcher.end();

            // skippedText may contain only letters, it may be an empty string
            String skippedText = text.substring(lastEnd, currentStart);
            if (!skippedText.equals("")) {
                tokens.add(new Token<AffiliationLabel>(skippedText,
                        asciiIndices.get(lastEnd), asciiIndices.get(currentStart)));
            }

            // matched text may be a sequence of digits or a single non-alphanumeric character
            String matchedText = text.substring(currentStart, currentEnd);
            // ignore whitespace
            if (!matchedText.matches("\\s")) {
                tokens.add(new Token<AffiliationLabel>(matchedText,
                        asciiIndices.get(currentStart), asciiIndices.get(currentEnd)));
            }

            lastEnd = currentEnd;
        }

        String skippedText = text.substring(lastEnd, text.length());
        if (!skippedText.equals("")) {
            tokens.add(new Token<AffiliationLabel>(skippedText,
                    asciiIndices.get(lastEnd), asciiIndices.get(text.length())));
        }

        return tokens;
    }

    /**
     * Returns a list of indices of all ASCII characters in the text
     *
     * @param text
     * @return list of indices
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
     * Returns a string formed by the characters in the text with the given
     * indices.
     *
     * @param text
     * @param indices
     * @return a substring
     */
    private static String getSubstring(String text, List<Integer> indices) {
        StringBuilder substringBuilder = new StringBuilder();
        for (int i : indices) {
            substringBuilder.append(text.charAt(i));
        }
        return substringBuilder.toString();
    }

    /**
     * Tokenizes a normalized string. The string should be in NFKD, so that the
     * accents are separated from the letters.
     *
     * Non-ASCII characters are ignored. Words separated by spaces are treated
     * as different tokens. Spaces are not kept as tokens. Sequences of digits
     * are separated from sequences of letters. Non-alphanumeric characters are
     * all treated as separate tokens.
     *
     * @param text the text to tokenize, should be in NFKD
     * @return list of tokens
     */
    @Override
    public List<Token<AffiliationLabel>> tokenize(String text) {
        List<Integer> asciiIndices = getAsciiSubstringIndices(text);
        String asciiText = getSubstring(text, asciiIndices);
        asciiIndices.add(text.length()); // Guardian index
        return asciiTextToTokens(asciiText, asciiIndices);
    }
}
