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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 *
 * @author Dominika Tkaczyk
 */
public class AffiliationCountryEnhancer implements Enhancer {

    private static final String COUNTRIES_FILE = "/pl/edu/icm/cermine/metadata/affiliation/countries.txt";
    private static final String INSTITUTIONS_FILE = "/pl/edu/icm/cermine/metadata/affiliation/institutions.txt";
    
    private static final List<String> countries = new ArrayList<String>();
    static {
        readHints(COUNTRIES_FILE, countries);
    }
    private static final List<String> institutions = new ArrayList<String>();
    static {
        readHints(INSTITUTIONS_FILE, institutions);
    }
    
    @Override
    public void enhanceMetadata(BxDocument document, DocumentMetadata metadata, Set<EnhancedField> enhancedFields) {
        for (DocumentAffiliation affiliation : metadata.getAffiliations()) {
            String text = affiliation.getRawText();
            
            String[] tokens = text.split("((?<=[,;])|(?=[,;]))");
            int index = 0;

            List<Integer> fieldIndexes = new ArrayList<Integer>();
            List<Token> fieldTokens = new ArrayList<Token>();
            
            for (String token : tokens) {
                if (token.equals(",") || token.equals(";")) {
                    index += token.length();
                    continue;
                }   
                if (countries.contains(normalize(token))) {
                    fieldIndexes.add(index);
                    fieldTokens.add(new Token(index, index+token.length(), DocumentAffiliation.TAG_COUNTRY));
                }
                index += token.length();
            }

            index = 0;
            for (String token : tokens) {
                if (token.equals(",") || token.equals(";")) {
                    index += token.length();
                    continue;
                }
                if (!fieldIndexes.contains(index)) {
                    for (String institution : institutions) {
                        if (token.toLowerCase().contains(institution)) {
                            fieldTokens.add(new Token(index, index+token.length(), DocumentAffiliation.TAG_INSTITUTION));
                            break;
                        }
                    }
                }
                index += token.length();
            }
            
            index = 0;
            for (String token : tokens) {
                if (token.equals(",") || token.equals(";")) {
                    index += token.length();
                    continue;
                }
                if (!fieldIndexes.contains(index)) {    
                    int index2 = index;
                    String[] tokens2 = token.split("((?<=[.,;()])|(?=[.,;()]))");
                    for (String token2 : tokens2) {
                        if (countries.contains(normalize(token2))) {
                            fieldTokens.add(new Token(index2, index2+token2.length(), DocumentAffiliation.TAG_COUNTRY));
                        }
                        index2 += token2.length();
                    }
                }
                index += token.length();
            }

            Collections.sort(fieldTokens, new Comparator<Token>() {

                @Override
                public int compare(Token t, Token t1) {
                    return Integer.valueOf(t.begin).compareTo(Integer.valueOf(t1.begin));
                }
            });
            
            int bIndex = -1;
            int eIndex = -1;
            String cTag = null;
            for (int i = 0; i < fieldTokens.size(); i++) {
                if (bIndex == -1) {
                    bIndex = fieldTokens.get(i).begin;
                    eIndex = fieldTokens.get(i).end;
                    cTag = fieldTokens.get(i).tag;
                } else {
                    if (cTag.equals(fieldTokens.get(i).tag) && (eIndex >=  fieldTokens.get(i).begin
                            || text.substring(eIndex, fieldTokens.get(i).begin).matches(" *[,;.()] *"))) {
                        eIndex = fieldTokens.get(i).end;
                    } else {
                        affiliation.addToken(bIndex, eIndex, cTag);
                        bIndex = fieldTokens.get(i).begin;
                        eIndex = fieldTokens.get(i).end;
                        cTag = fieldTokens.get(i).tag;
                    }
                }
            }
            if (bIndex != -1) {
                affiliation.addToken(bIndex, eIndex, cTag);
            }
            
        }
    }
    
    public static void readHints(String file, List<String> list) {
        InputStream is = AffiliationCountryEnhancer.class.getResourceAsStream(file);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while((line = in.readLine()) != null) {
                list.add(normalize(line));
            }
        } catch (IOException ex) {
        } finally {
            try {
                is.close();
            } catch (IOException ex1) {}
        }
    }
    
    private static String normalize(String string) {
        return string.toLowerCase().replaceAll("[^a-z]", "").trim();
    }
    
    private static class Token {
        int begin;
        int end;
        String tag;

        public Token(int begin, int end, String tag) {
            this.begin = begin;
            this.end = end;
            this.tag = tag;
        }
        
    }
}
