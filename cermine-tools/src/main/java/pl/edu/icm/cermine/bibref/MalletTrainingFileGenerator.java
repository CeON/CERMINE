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

package pl.edu.icm.cermine.bibref;

import com.google.common.collect.Lists;
import java.io.*;
import java.util.Map.Entry;
import java.util.*;
import org.jdom.JDOMException;
import org.xml.sax.InputSource;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.bibref.parsing.tools.NlmCitationExtractor;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class MalletTrainingFileGenerator {

    private static final int MIN_TERM_COUNT = 5;

    public static void main(String[] args) throws JDOMException, IOException {
        File inputFile = new File(args[0]);
        List<Citation> citations = new ArrayList<Citation>();
        Map<String, Integer> termCounts = new HashMap<String, Integer>();
       
        InputStream is = null;
        try {
            is = new FileInputStream(inputFile);
            InputSource source = new InputSource(is);
            citations = NlmCitationExtractor.extractCitations(source);
        } finally {
             if (is != null) {
                is.close();
            }
        }

        for (Citation citation : citations) {
            for (CitationToken citationToken : citation.getTokens()) {
                if (citationToken.getText().matches("^[a-zA-Z]+$")) {
                    String term = citationToken.getText().toLowerCase(Locale.ENGLISH);
                    if (termCounts.get(term) == null) {
                        termCounts.put(term, 0);
                    }
                    termCounts.put(term, termCounts.get(term) + 1);
                }
            }
        }
        
        List<Entry<String, Integer>> wordCounts = Lists.newArrayList(termCounts.entrySet());
        Collections.sort(wordCounts, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> t1, Entry<String, Integer> t2) {
                if (t1.getValue().compareTo(t2.getValue()) != 0) {
                    return t2.getValue().compareTo(t1.getValue());
                }
                return t1.getKey().compareTo(t2.getKey());
            }
        });

        Set<String> additionalFeatures = new HashSet<String>();
        for (Entry<String, Integer> wordCount : wordCounts) {
            if (wordCount.getValue() > MIN_TERM_COUNT) {
                additionalFeatures.add(wordCount.getKey());
            }
        }

        Writer featuresWriter = null;
        Writer termsWriter = null;
            
        try {
            featuresWriter = new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8");
            termsWriter = new OutputStreamWriter(new FileOutputStream(args[2]), "UTF-8");

            for (String s : additionalFeatures) {
                termsWriter.write(s);
                termsWriter.write("\n");
            }
            termsWriter.flush();
            termsWriter.close();
            
            for (Citation citation : citations) {
                List<String> tokens = CitationUtils.citationToMalletInputFormat(citation, additionalFeatures);
                for (String token : tokens) {
                    featuresWriter.write(token);
                    featuresWriter.write("\n");
                }
                featuresWriter.write("\n");
            }

            featuresWriter.flush();
        } finally {
            try {
                if (featuresWriter != null) {
                    featuresWriter.close();
                }
            } finally {
                if (termsWriter != null) {
                    termsWriter.close();
                }
            }
        }
    }

    private MalletTrainingFileGenerator() {
    }
}
