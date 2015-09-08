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

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import java.io.StringReader;
import java.util.*;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationPosition;

/**
 * A class for finding the text contexts of references based on their positions
 * in the document's text.
 *
 * @author Dominika Tkaczyk
 */
public class CitationContextFinder {
    
    public List<List<String>> findContext(String fullText, List<List<CitationPosition>> positions) {
        Map<Integer, Integer> wordsIndexes = new HashMap<Integer, Integer>();
        DocumentPreprocessor procesor = new DocumentPreprocessor(new StringReader(fullText));
        Iterator<List<HasWord>> iterator = procesor.iterator();
        while (iterator.hasNext()) {
            List<HasWord> wordList = iterator.next();
            int min = fullText.length();
            int max = 0;
            for (HasWord word : wordList) {               
                CoreLabel cl = (CoreLabel) word;
                if (min > cl.beginPosition()) {
                    min = cl.beginPosition();
                }
                if (max < cl.endPosition()) {
                    max = cl.endPosition();
                }
            }
            wordsIndexes.put(min, max);
        }
        List<List<String>> contexts = new ArrayList<List<String>>();
        
        for (List<CitationPosition> position : positions) {
            List<String> context = new ArrayList<String>();
            for (CitationPosition pos : position) {
                for (Map.Entry<Integer, Integer> entry : wordsIndexes.entrySet()) {
                    if (pos.getStartRefPosition() >= entry.getKey()
                            && pos.getStartRefPosition() < entry.getValue()) {
                        context.add(fullText.substring(entry.getKey(), entry.getValue()));
                    }
                }
            }
            contexts.add(context);
        }
        return contexts;
    }
    
}
