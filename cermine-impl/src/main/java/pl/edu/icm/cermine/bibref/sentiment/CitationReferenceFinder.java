/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.bibref.sentiment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationContext;
import pl.edu.icm.cermine.tools.CharacterUtils;
import pl.edu.icm.cermine.tools.TextUtils;

/**
 * A class for extracting references from the document's full text.
 *
 * @author Dominika Tkaczyk
 */
public class CitationReferenceFinder {
    
    public List<CitationContext> findReferences(String fullText, BibEntry citation) {
        List<CitationContext> contexts = new ArrayList<CitationContext>();
        Pattern numberPattern = Pattern.compile("^[^\\d]*(\\d+)");
        Matcher numberMatcher = numberPattern.matcher(citation.getText());
        String number = null;
        if (numberMatcher.find()) {
            number = numberMatcher.group(1);
        }
        
        Pattern refPattern = Pattern.compile("\\[([, \\d" + String.valueOf(CharacterUtils.DASH_CHARS) + "]+)\\]");
        Matcher refMatcher = refPattern.matcher(fullText);

        while (refMatcher.find()) {
            String[] matched = refMatcher.group(1).replaceAll(" ", "").split(",");
            for (String match : matched) {
                if (number.equals(match)) {
                    CitationContext context = new CitationContext();
                    context.setKey(citation.getKey());
                    context.setStartRefPosition(refMatcher.start(1));
                    context.setEndRefPosition(refMatcher.end(1));
                    contexts.add(context);
                } else {
                    Pattern rangePattern = Pattern.compile("^(\\d+)[" + String.valueOf(CharacterUtils.DASH_CHARS) + "](\\d+)$");
                    Matcher rangeMatcher = rangePattern.matcher(match);
                    if (rangeMatcher.find()) {
                        int lower = Integer.parseInt(rangeMatcher.group(1));
                        int upper = Integer.parseInt(rangeMatcher.group(2));
                        if (TextUtils.isNumberBetween(number, lower, upper+1)) {
                            CitationContext context = new CitationContext();
                            context.setKey(citation.getKey());
                            context.setStartRefPosition(refMatcher.start(1));
                            context.setEndRefPosition(refMatcher.end(1));
                            contexts.add(context);
                        }
                    }
                }
            }
        }
        
        return contexts;
    }
    
}
