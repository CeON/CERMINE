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

package pl.edu.icm.cermine.bibref.parsing.tools;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.model.BibEntryFieldType;
import pl.edu.icm.cermine.bibref.model.BibEntryType;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.cermine.tools.PatternUtils;
import pl.edu.icm.cermine.tools.classification.general.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;

/**
 * Citation utility class.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class CitationUtils {

    private static final Map<CitationTokenLabel, BibEntryFieldType> TO_BIBENTRY =
            new EnumMap<CitationTokenLabel, BibEntryFieldType>(CitationTokenLabel.class);

    static {
        TO_BIBENTRY.put(CitationTokenLabel.ARTICLE_TITLE,   BibEntryFieldType.TITLE);
        TO_BIBENTRY.put(CitationTokenLabel.CONTENT,         BibEntryFieldType.CONTENTS);
        TO_BIBENTRY.put(CitationTokenLabel.EDITION,         BibEntryFieldType.EDITION);
        TO_BIBENTRY.put(CitationTokenLabel.PUBLISHER_NAME,  BibEntryFieldType.PUBLISHER);
        TO_BIBENTRY.put(CitationTokenLabel.PUBLISHER_LOC,   BibEntryFieldType.LOCATION);
        TO_BIBENTRY.put(CitationTokenLabel.SERIES,          BibEntryFieldType.SERIES);
        TO_BIBENTRY.put(CitationTokenLabel.SOURCE,          BibEntryFieldType.JOURNAL);
        TO_BIBENTRY.put(CitationTokenLabel.URI,             BibEntryFieldType.URL);
        TO_BIBENTRY.put(CitationTokenLabel.VOLUME,          BibEntryFieldType.VOLUME);
        TO_BIBENTRY.put(CitationTokenLabel.YEAR,            BibEntryFieldType.YEAR);
        TO_BIBENTRY.put(CitationTokenLabel.ISSUE,           BibEntryFieldType.NUMBER);
        TO_BIBENTRY.put(CitationTokenLabel.DOI,             BibEntryFieldType.DOI);
        TO_BIBENTRY.put(CitationTokenLabel.PMID,            BibEntryFieldType.PMID);
    }

    private CitationUtils() {}
    
    public static Citation stringToCitation(String citation) {
        List<CitationToken> tokenList = new ArrayList<CitationToken>();

        String text = citation;
        int actIndex = 0;
        while (text.length() > 0) {
            int end = 1;
            if (Character.isLetterOrDigit(text.charAt(0))) {
                end = 0;
                while (text.length() > end && Character.isLetterOrDigit(text.charAt(end))) {
                    end++;
                }
            }
            String token = text.substring(0, end);
            if (!token.matches("\\s+")) {
                tokenList.add(new CitationToken(token, actIndex, actIndex + end));
            }
            text = text.substring(end);
            actIndex += end;
        }

        return new Citation(citation, tokenList);
    }

    public static BibEntry citationToBibref(Citation citation) {
        String text = citation.getText();
        Pattern doiPattern = Pattern.compile(".*(" + PatternUtils.DOI_PATTERN + ").*");
        Matcher m = doiPattern.matcher(text);
        if (m.matches()) {
            for (CitationToken t :citation.getTokens()) {
                if (t.getStartIndex() >= m.start(1) && t.getEndIndex() <= m.end(1)) {
                    t.setLabel(CitationTokenLabel.DOI);
                }
            }
        }
        Pattern pmidPattern = Pattern.compile(".*pmid\\s*:?\\s*(\\d+).*", Pattern.CASE_INSENSITIVE);
        m = pmidPattern.matcher(text);
        if (m.matches()) {
            for (CitationToken t :citation.getTokens()) {
                if (t.getStartIndex() >= m.start(1) && t.getEndIndex() <= m.end(1)) {
                    t.setLabel(CitationTokenLabel.PMID);
                }
            }
        }
       
        List<CitationToken> tokens = new ArrayList<CitationToken>();
        CitationToken token = null;
        for (CitationToken actToken : citation.getTokens()) {
            CitationTokenLabel actLabel = actToken.getLabel();

            if (TO_BIBENTRY.containsKey(actLabel) || actLabel.equals(CitationTokenLabel.PAGEF)
                    || actLabel.equals(CitationTokenLabel.PAGEL) || actLabel.equals(CitationTokenLabel.GIVENNAME)
                    || actLabel.equals(CitationTokenLabel.SURNAME)) {
                if (token != null && actLabel.equals(token.getLabel())) {
                    token.setEndIndex(actToken.getEndIndex());
                    token.setText(citation.getText().substring(token.getStartIndex(), token.getEndIndex()));
                } else {
                    if (token != null) {
                        tokens.add(token);
                    }
                    token = new CitationToken(actToken.getText(), actToken.getStartIndex(), actToken.getEndIndex(),
                            actToken.getLabel());
                }
            } else {
                if (token != null) {
                    tokens.add(token);
                    token = null;
                }
            }
        }

        if (token != null) {
            tokens.add(token);
        }

        BibEntry bibEntry = new BibEntry(BibEntryType.ARTICLE);
        String lcText = text.toLowerCase(Locale.ENGLISH);

        if (lcText.contains("tech report") || lcText.contains("technical report")) {
            bibEntry.setType(BibEntryType.TECHREPORT);
        } else if (lcText.contains("proceeding") || lcText.contains("conference")
                || lcText.contains("workshop") || lcText.contains("proc. ") 
                || lcText.contains("conf. ")) {
            bibEntry.setType(BibEntryType.PROCEEDINGS);
        }

        bibEntry.setText(text);
        
        int i = 0;
        while (i < tokens.size()) {
            CitationToken actToken = tokens.get(i);
            CitationTokenLabel actLabel = actToken.getLabel();

            CitationToken nextToken = null;
            if (i < tokens.size() - 1) {
                nextToken = tokens.get(i + 1);
            }

            if (TO_BIBENTRY.containsKey(actLabel)) {
                String value = text.substring(actToken.getStartIndex(), actToken.getEndIndex());
                bibEntry.addField(TO_BIBENTRY.get(actLabel), value, actToken.getStartIndex(), actToken.getEndIndex());
                i++;
            } else if (actLabel.equals(CitationTokenLabel.PAGEF)) {
                if (nextToken != null && nextToken.getLabel().equals(CitationTokenLabel.PAGEL)) {
                    String pagef = text.substring(actToken.getStartIndex(), actToken.getEndIndex());
                    String pagel = text.substring(nextToken.getStartIndex(), nextToken.getEndIndex());
                    String value = pagef + "--" + pagel;

                    bibEntry.addField(BibEntryFieldType.PAGES, value, actToken.getStartIndex(), nextToken.getEndIndex());
                    i += 2;
                } else {
                    String value = text.substring(actToken.getStartIndex(), actToken.getEndIndex());
                    bibEntry.addField(BibEntryFieldType.PAGES, value, actToken.getStartIndex(), actToken.getEndIndex());
                    i++;
                }
            } else if (actLabel.equals(CitationTokenLabel.PAGEL)) {
                String value = text.substring(actToken.getStartIndex(), actToken.getEndIndex());
                bibEntry.addField(BibEntryFieldType.PAGES, value, actToken.getStartIndex(), actToken.getEndIndex());
                i++;
            } else if (actLabel.equals(CitationTokenLabel.GIVENNAME)) {
                if (nextToken != null && nextToken.getLabel().equals(CitationTokenLabel.SURNAME)) {
                    String value = text.substring(nextToken.getStartIndex(), nextToken.getEndIndex()) + ", "
                            + text.substring(actToken.getStartIndex(), actToken.getEndIndex());
                    bibEntry.addField(BibEntryFieldType.AUTHOR, value, actToken.getStartIndex(), nextToken.getEndIndex());
                    i += 2;
                } else {
                    String value = text.substring(actToken.getStartIndex(), actToken.getEndIndex());
                    bibEntry.addField(BibEntryFieldType.AUTHOR, value, actToken.getStartIndex(), actToken.getEndIndex());
                    i++;
                }
            } else if (actLabel.equals(CitationTokenLabel.SURNAME)) {
                if (nextToken != null && nextToken.getLabel().equals(CitationTokenLabel.GIVENNAME)) {
                    String value = text.substring(actToken.getStartIndex(), actToken.getEndIndex()) + ", "
                            + text.substring(nextToken.getStartIndex(), nextToken.getEndIndex());
                    bibEntry.addField(BibEntryFieldType.AUTHOR, value, actToken.getStartIndex(), nextToken.getEndIndex());
                    i += 2;
                } else {
                    String value = text.substring(actToken.getStartIndex(), actToken.getEndIndex());
                    bibEntry.addField(BibEntryFieldType.AUTHOR, value, actToken.getStartIndex(), actToken.getEndIndex());
                    i++;
                }
            }
        }
        
        return bibEntry;
    }
    
    public static List<String> citationToMalletInputFormat(Citation citation) {
        return citationToMalletInputFormat(citation, null);
    }
    
    public static List<String> citationToMalletInputFormat(Citation citation,
            Set<String> terms) {
        List<String> trainingExamples = new ArrayList<String>();

        FeatureVectorBuilder vectorBuilder = FeatureList.VECTOR_BUILDER;
        
        List<CitationToken> tokens = citation.getTokens();
        List<FeatureVector> featureVectors = new ArrayList<FeatureVector>();
        for (CitationToken token : tokens) {
            FeatureVector featureVector = vectorBuilder.getFeatureVector(token, citation);
            for (String featureName : featureVector.getFeatureNames()) {
                if (Double.isNaN(featureVector.getValue(featureName))) {
                    throw new RuntimeException("Feature value is set to NaN: "+featureName);
                }
            }
            if (terms == null) {
                featureVector.addFeature(token.getText().toLowerCase(Locale.ENGLISH), 1);
            } else if (terms.contains(token.getText().toLowerCase(Locale.ENGLISH))) {
                featureVector.addFeature(token.getText().toLowerCase(Locale.ENGLISH), 1);
            }
            featureVectors.add(featureVector);
        }
        
        for (int i = 0; i < tokens.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            
            stringBuilder.append(tokens.get(i).getLabel());
            stringBuilder.append(" ---- ");
               
            if (i >= 2) {
                for (String n : featureVectors.get(i-2).getFeatureNames()) {
                    if (featureVectors.get(i-2).getValue(n) > Double.MIN_VALUE) {
                        stringBuilder.append(n);
                        stringBuilder.append("@-2 ");
                    }
                }
            }
            if (i >= 1) {
                for (String n : featureVectors.get(i-1).getFeatureNames()) {
                    if (featureVectors.get(i-1).getValue(n) > Double.MIN_VALUE) {
                        stringBuilder.append(n);
                        stringBuilder.append("@-1 ");
                    }
                }
            }
            for (String n : featureVectors.get(i).getFeatureNames()) {
                if (featureVectors.get(i).getValue(n) > Double.MIN_VALUE) {
                    stringBuilder.append(n);
                    stringBuilder.append(" ");
                }
            }
            if (i < featureVectors.size()-1) {
                for (String n : featureVectors.get(i+1).getFeatureNames()) {
                    if (featureVectors.get(i+1).getValue(n) > Double.MIN_VALUE) {
                        stringBuilder.append(n);
                        stringBuilder.append("@1 ");
                    }
                }
            }
            if (i < featureVectors.size()-2) {
                for (String n : featureVectors.get(i+2).getFeatureNames()) {
                    if (featureVectors.get(i+2).getValue(n) > Double.MIN_VALUE) {
                        stringBuilder.append(n);
                        stringBuilder.append("@2 ");
                    }
                }
            }
            while (stringBuilder.length() > 0 && Character.isWhitespace(stringBuilder.charAt(stringBuilder.length() - 1))) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            trainingExamples.add(stringBuilder.toString());
        }
        
        return trainingExamples; 
    }
}
