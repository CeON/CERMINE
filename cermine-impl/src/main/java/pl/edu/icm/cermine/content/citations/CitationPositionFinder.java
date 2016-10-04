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

package pl.edu.icm.cermine.content.citations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.tools.CharacterUtils;
import pl.edu.icm.cermine.tools.TextUtils;

/**
 * A class for extracting citation positions from the document's full text.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CitationPositionFinder {
    
    public List<List<CitationPosition>> findReferences(String fullText, List<BibEntry> citations) {
        DocumentPositions docPositions = new DocumentPositions(fullText.length(), citations);
        for (BibEntry citation: citations) {
            findByNumber(fullText, citation, "\\[", "\\]", docPositions);
        }
        List<List<CitationPosition>> positionsBySquare = docPositions.getPositions();
        
        docPositions = new DocumentPositions(fullText.length(), citations);
        for (BibEntry citation: citations) {
            findByAuthorYear(fullText, citation, docPositions);
            if (docPositions.getPositions(citation).isEmpty()) {
                findByAuthorYearFuzzy(fullText, citation, docPositions);
            }
        }
        List<List<CitationPosition>> positionsByAuthor = docPositions.getPositions();
        
        docPositions = new DocumentPositions(fullText.length(), citations);
        for (BibEntry citation: citations) {
            findByNumber(fullText, citation, "\\(", "\\)", docPositions);
        }
        List<List<CitationPosition>> positionsByRound = docPositions.getPositions();
        
        List<List<CitationPosition>> positions = positionsByAuthor;
        if (sumOfSizes(positionsBySquare) > sumOfSizes(positions)) {
            positions = positionsBySquare;
        }
        if (sumOfSizes(positionsByRound) > sumOfSizes(positions)) {
            positions = positionsByRound;
        }
        
        return positions;
    }

    private void findByAuthorYear(String fullText, BibEntry citation, DocumentPositions positions) {
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
                    positions.addPosition(citation, refMatcher.start(), refMatcher.end());
                }
            }
        }
        
        refPattern = Pattern.compile("([A-Z][^\\s\\.]+)\\s+et\\s+al\\.\\s+\\((\\d{4})\\)");
        refMatcher = refPattern.matcher(fullText);
        while (refMatcher.find()) {
            String year = refMatcher.group(2);
            if (!TextUtils.isNumberBetween(year, 1700, 2100) || !citation.getText().contains(year)) {
                continue;
            }
            if (citation.getText().startsWith(refMatcher.group(1))) {
                positions.addPosition(citation, refMatcher.start(), refMatcher.end());
            }
        }
        
        refPattern = Pattern.compile("([A-Z][^\\s\\.]+)\\s+(and|&)\\s+[A-Z][^\\s\\.]+\\s+\\((\\d{4})\\)");
        refMatcher = refPattern.matcher(fullText);
        while (refMatcher.find()) {
            String year = refMatcher.group(3);
            if (!TextUtils.isNumberBetween(year, 1700, 2100) || !citation.getText().contains(year)) {
                continue;
            }
            if (citation.getText().startsWith(refMatcher.group(1))) {
                positions.addPosition(citation, refMatcher.start(), refMatcher.end());
            }
        }
        
        refPattern = Pattern.compile("([A-Z][^\\s\\.]+)\\s+\\((\\d{4})\\)");
        refMatcher = refPattern.matcher(fullText);
        while (refMatcher.find()) {
            String year = refMatcher.group(2);
            if (!TextUtils.isNumberBetween(year, 1700, 2100) || !citation.getText().contains(year)) {
                continue;
            }
            if (citation.getText().startsWith(refMatcher.group(1))) {
                positions.addPosition(citation, refMatcher.start(), refMatcher.end());
            }
        }
    }
    
    private void findByAuthorYearFuzzy(String fullText, BibEntry citation, DocumentPositions positions) {
        Pattern namePattern = Pattern.compile("^\\w+");
        Matcher nameMatcher = namePattern.matcher(citation.getText());
        if (nameMatcher.find()) {
            String name = nameMatcher.group();
            
            Pattern refPattern = Pattern.compile(name + "\\D{1,30}(\\d{4})", Pattern.CASE_INSENSITIVE);
            Matcher refMatcher = refPattern.matcher(fullText);
            while (refMatcher.find()) {
                String year = refMatcher.group(1);
                if (!TextUtils.isNumberBetween(year, 1700, 2100) || !citation.getText().contains(year)) {
                    continue;
                }
                positions.addPosition(citation, refMatcher.start(), refMatcher.end());
            }
        }
    }
    
    private void findByNumber(String fullText, BibEntry citation, String leftBracket, String rightBracket,
            DocumentPositions positions) {
        Pattern numberPattern = Pattern.compile("^[^\\d]{0,10}(\\d{1,5})");
        Matcher numberMatcher = numberPattern.matcher(citation.getText());
        String number;
        if (numberMatcher.find()) {
            number = numberMatcher.group(1);
        } else {
            return;
        }
        
        Pattern refPattern = Pattern.compile(leftBracket + "([,\\s\\d" + String.valueOf(CharacterUtils.DASH_CHARS) + "]+)" + rightBracket);
        Matcher refMatcher = refPattern.matcher(fullText);
        while (refMatcher.find()) {
            String[] matched = refMatcher.group(1).replaceAll("\\s", "").split(",");
            for (String match : matched) {
                if (number.equals(match)) {
                    positions.addPosition(citation, refMatcher.start(1), refMatcher.end(1));
                } else {
                    Pattern rangePattern = Pattern.compile("^(\\d{1,5})[" + String.valueOf(CharacterUtils.DASH_CHARS) + "](\\d{1,5})$");
                    Matcher rangeMatcher = rangePattern.matcher(match);
                    if (rangeMatcher.find()) {
                        try {
                            int lower = Integer.parseInt(rangeMatcher.group(1));
                            int upper = Integer.parseInt(rangeMatcher.group(2));
                            if (TextUtils.isNumberBetween(number, lower, upper+1)) {
                                positions.addPosition(citation, refMatcher.start(1), refMatcher.end(1));
                            }
                        } catch (NumberFormatException e) {}
                    }
                }
            }
        }
    }

    private int sumOfSizes(List<List<CitationPosition>> positions) {
        int sum = 0;
        for (List<CitationPosition> pos : positions) {
            sum += pos.size();
        }
        return sum;
    }
    
    private static class DocumentPositions {
        private final boolean[] covered;
        List<CitationPosition> positions = new ArrayList<CitationPosition>();
        List<BibEntry> citations;
        Map<BibEntry, List<CitationPosition>> citationPositions = new HashMap<BibEntry, List<CitationPosition>>();

        public DocumentPositions(int size, List<BibEntry> citations) {
            covered = new boolean[size];
            this.citations = citations;
            positions = new ArrayList<CitationPosition>();
            citationPositions = new HashMap<BibEntry, List<CitationPosition>>();
            for (BibEntry citation : citations) {
                citationPositions.put(citation, new ArrayList<CitationPosition>());
            }
        }

        private void addPosition(BibEntry citation, int start, int end) {
            boolean exists = false;
            for (int i = start; i < end; i++) {
                if (covered[i]) {
                    exists = true;
                }
            }
            if (exists) {
                for (CitationPosition pos : positions) {
                    if (start == pos.getStartRefPosition() && end == pos.getEndRefPosition()) {
                        exists = false;
                    }
                }
            }
            if (!exists) {
                CitationPosition position = new CitationPosition();
                position.setStartRefPosition(start);
                position.setEndRefPosition(end);
                positions.add(position);
                citationPositions.get(citation).add(position);
                for (int i = start; i < end; i++) {
                    covered[i] = true;
                }   
            }
        }

        private List<List<CitationPosition>> getPositions() {
            List<List<CitationPosition>> ret = new ArrayList<List<CitationPosition>>();
            for (BibEntry citation : citations) {
                ret.add(citationPositions.get(citation));
            }
            return ret;
        }

        private List<CitationPosition> getPositions(BibEntry citation) {
            return citationPositions.get(citation);
        }
        
    }
    
}
 