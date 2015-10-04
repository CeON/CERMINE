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
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationPosition;
import pl.edu.icm.cermine.tools.CharacterUtils;
import pl.edu.icm.cermine.tools.TextUtils;

/**
 * A class for extracting citation positions from the document's full text.
 *
 * @author Dominika Tkaczyk
 */
public class CitationPositionFinder {
    
    public List<List<CitationPosition>> findReferences(String fullText, List<BibEntry> citations) {
        List<List<CitationPosition>> positions = new ArrayList<List<CitationPosition>>();
        for (BibEntry citation: citations) {
            List<CitationPosition> pos = findByNumber(fullText, citation, "\\[", "\\]");
            positions.add(pos);
        }
        if (sumOfSizes(positions) < citations.size()) {
            positions.clear();
            for (BibEntry citation: citations) {
                List<CitationPosition> pos = findByAuthorYear(fullText, citation);
                positions.add(pos);
            }
        }
        if (sumOfSizes(positions) < citations.size()) {
            positions.clear();
            for (BibEntry citation: citations) {
                List<CitationPosition> pos = findByAuthorYearFuzzy(fullText, citation);
                positions.add(pos);
            }
        }
        if (sumOfSizes(positions) < citations.size()) {
            positions.clear();
            for (BibEntry citation: citations) {
                List<CitationPosition> pos = findByNumber(fullText, citation, "\\(", "\\)");
                positions.add(pos);
            }
        }
  
        if (sumOfSizes(positions) < citations.size()) {
            for (List<CitationPosition> pos : positions) {
                pos.clear();
            }
        }
        
        return positions;
    }
    
    public List<CitationPosition> findReferences(String fullText, BibEntry citation) {
        List<CitationPosition> positions = findByNumber(fullText, citation, "\\[", "\\]");
        if (positions.isEmpty()) {
            positions = findByAuthorYear(fullText, citation);
        }
        if (positions.isEmpty()) {
            positions = findByAuthorYearFuzzy(fullText, citation);
        }
        if (positions.isEmpty()) {
            positions = findByNumber(fullText, citation, "\\(", "\\)");
        }
        return positions;
    }
    
    private List<CitationPosition> findByAuthorYear(String fullText, BibEntry citation) {
        List<CitationPosition> positions = new ArrayList<CitationPosition>();
        
        List<String> tokens = new ArrayList<String>();
        for (CitationToken token : CitationUtils.stringToCitation(citation.getText()).getTokens()) {
            tokens.add(token.getText().toLowerCase().trim());
        }
           
        Pattern refPattern = Pattern.compile("\\([^\\(\\)]+\\d{4}[^\\(\\)]*\\)");
        Matcher refMatcher = refPattern.matcher(fullText);
        while (refMatcher.find()) {
            String reference = refMatcher.group().toLowerCase().replaceAll("^.", "").replaceAll(".$", "");
            String[] refs = reference.split(";");
            for (String ref : refs) {
                Pattern namePattern = Pattern.compile("[a-z]+");
                Matcher nameMatcher = namePattern.matcher(ref);
                Pattern yearPattern = Pattern.compile("\\d{4}");
                Matcher yearMatcher = yearPattern.matcher(ref);
                boolean nameFound = false;
                boolean yearFound = false;
                while (nameMatcher.find()){
                    String name = nameMatcher.group();
                    if (tokens.contains(name) && tokens.indexOf(name) < 10) {
                        nameFound = true;
                    }
                }
                while (yearMatcher.find()) {
                    String year = yearMatcher.group();
                    if (citation.getText().contains(year)) {
                        yearFound = true;
                    }
                }
                if (nameFound && yearFound) {
                    addPosition(positions, refMatcher.start(), refMatcher.end());
                }
            }
        }
        
        refPattern = Pattern.compile("([A-Z][^\\s\\.]+)\\s+et\\s+al\\.\\s+\\((\\d\\d\\d\\d)\\)");
        refMatcher = refPattern.matcher(fullText);
        while (refMatcher.find()) {
            String year = refMatcher.group(2);
            if (!TextUtils.isNumberBetween(year, 1700, 2100) || !citation.getText().contains(year)) {
                continue;
            }
            if (citation.getText().startsWith(refMatcher.group(1))) {
                addPosition(positions, refMatcher.start(), refMatcher.end());
            }
        }
        
        refPattern = Pattern.compile("([A-Z][^\\s\\.]+)\\s+(and|&)\\s+[A-Z][^\\s\\.]+\\s+\\((\\d\\d\\d\\d)\\)");
        refMatcher = refPattern.matcher(fullText);
        while (refMatcher.find()) {
            String year = refMatcher.group(3);
            if (!TextUtils.isNumberBetween(year, 1700, 2100) || !citation.getText().contains(year)) {
                continue;
            }
            if (citation.getText().startsWith(refMatcher.group(1))) {
                addPosition(positions, refMatcher.start(), refMatcher.end());
            }
        }
        
        refPattern = Pattern.compile("([A-Z][^\\s\\.]+)\\s+\\((\\d\\d\\d\\d)\\)");
        refMatcher = refPattern.matcher(fullText);
        while (refMatcher.find()) {
            String year = refMatcher.group(2);
            if (!TextUtils.isNumberBetween(year, 1700, 2100) || !citation.getText().contains(year)) {
                continue;
            }
            if (citation.getText().startsWith(refMatcher.group(1))) {
                addPosition(positions, refMatcher.start(), refMatcher.end());
            }
        }
        
        return positions;
    }
    
    private List<CitationPosition> findByAuthorYearFuzzy(String fullText, BibEntry citation) {
        List<CitationPosition> positions = new ArrayList<CitationPosition>();
        
        Pattern namePattern = Pattern.compile("^\\w+");
        Matcher nameMatcher = namePattern.matcher(citation.getText());
        if (nameMatcher.find()) {
            String name = nameMatcher.group();
            
            Pattern refPattern = Pattern.compile(name + "\\D{1,30}(\\d{4})");
            Matcher refMatcher = refPattern.matcher(fullText);
            while (refMatcher.find()) {
                String year = refMatcher.group(1);
                if (!TextUtils.isNumberBetween(year, 1700, 2100) || !citation.getText().contains(year)) {
                    continue;
                }
                addPosition(positions, refMatcher.start(), refMatcher.end());
            }
        }
        
        return positions;
    }
    
    private List<CitationPosition> findByNumber(String fullText, BibEntry citation, 
            String leftBracket, String rightBracket) {
        List<CitationPosition> positions = new ArrayList<CitationPosition>();
        Pattern numberPattern = Pattern.compile("^[^\\d]{0,10}(\\d+)");
        Matcher numberMatcher = numberPattern.matcher(citation.getText());
        String number;
        if (numberMatcher.find()) {
            number = numberMatcher.group(1);
        } else {
            return positions;
        }
        
        Pattern refPattern = Pattern.compile(leftBracket + "([,\\s\\d" + String.valueOf(CharacterUtils.DASH_CHARS) + "]+)" + rightBracket);
        Matcher refMatcher = refPattern.matcher(fullText);
        while (refMatcher.find()) {
            String[] matched = refMatcher.group(1).replaceAll("\\s", "").split(",");
            for (String match : matched) {
                if (number.equals(match)) {
                    addPosition(positions, refMatcher.start(1), refMatcher.end(1));
                } else {
                    Pattern rangePattern = Pattern.compile("^(\\d+)[" + String.valueOf(CharacterUtils.DASH_CHARS) + "](\\d+)$");
                    Matcher rangeMatcher = rangePattern.matcher(match);
                    if (rangeMatcher.find()) {
                        int lower = Integer.parseInt(rangeMatcher.group(1));
                        int upper = Integer.parseInt(rangeMatcher.group(2));
                        if (TextUtils.isNumberBetween(number, lower, upper+1)) {
                            addPosition(positions, refMatcher.start(1), refMatcher.end(1));
                        }
                    }
                }
            }
        }
        
        return positions;
    }

    private void addPosition(List<CitationPosition> positions, int start, int end) {
        CitationPosition position = new CitationPosition();
        position.setStartRefPosition(start);
        position.setEndRefPosition(end);
        positions.add(position);
    }
    
    private int sumOfSizes(List<List<CitationPosition>> positions) {
        int sum = 0;
        for (List<CitationPosition> pos : positions) {
            sum += pos.size();
        }
        return sum;
    }
    
}
