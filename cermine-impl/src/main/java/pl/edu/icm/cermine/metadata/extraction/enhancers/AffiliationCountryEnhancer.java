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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 *
 * @author krusek
 */
public class AffiliationCountryEnhancer implements Enhancer {

    private static List<String> countries = new ArrayList<String>();
    static {
        readCountries();
    }
    
    @Override
    public void enhanceMetadata(BxDocument document, DocumentMetadata metadata, Set<EnhancedField> enhancedFields) {
        for (DocumentAffiliation affiliation : metadata.getAffiliations()) {
            String text = affiliation.getRawText();
            
            String[] tokens = text.split("((?<=[,;])|(?=[,;]))");
            int index = 0;
        
            List<Integer> begin = new ArrayList<Integer>();
            List<Integer> end = new ArrayList<Integer>();
            
            for (String token : tokens) {
                if (token.equals(",") || token.equals(";")) {
                    index += token.length();
                    continue;
                }
                
                if (countries.contains(normalize(token))) {
                    begin.add(index);
                    end.add(index+token.length());
                } else {
                    int index2 = index;
                    String[] tokens2 = token.split("((?<=[.,;()])|(?=[.,;()]))");
                    for (String token2 : tokens2) {
                        if (countries.contains(normalize(token2))) {
                            begin.add(index2);
                            end.add(index2+token2.length());
                        }
                        index2 += token2.length();
                    }
                }
                index += token.length();
            }
            
            int bIndex = -1;
            int eIndex = -1;
            for (int i = 0; i < begin.size(); i++) {
                if (bIndex == -1) {
                    bIndex = begin.get(i);
                    eIndex = end.get(i);
                } else {
                    if (eIndex >= begin.get(i) || text.substring(eIndex, begin.get(i)).matches(" *[,;.()] *")) {
                        eIndex = end.get(i);
                    } else {
                        affiliation.addCountry(bIndex, eIndex);
                        bIndex = -1;
                        eIndex = -1;
                    }
                }
            }
            if (bIndex != -1) {
                affiliation.addCountry(bIndex, eIndex);
            }
            
        }
    }
    
    public static void readCountries() {
        InputStream is = AffiliationCountryEnhancer.class.getResourceAsStream("/pl/edu/icm/cermine/metadata/affiliation/countries.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while((line = in.readLine()) != null) {
                countries.add(normalize(line));
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
    
}
