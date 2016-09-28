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

    private static final String NLM_DIR = "cermine-tests/out/";
    private static final String OUT_FILE = "crf-train.txt";
    private static final String OUT_FILE2 = "crf-train-words.txt";
    private static final int MIN_COUNT = 5;

    public static void main(String[] args) throws JDOMException, IOException {

        File dir = new File(NLM_DIR);
        FileWriter writer = null;
        try {
            List<Citation> allcitations = new ArrayList<Citation>();

            Map<String, Integer> wordMap = new HashMap<String, Integer>();

            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    continue;
                }
                if (!file.getName().endsWith(".xml")) {
                    continue;
                }

                InputStream is = null;
                List<Citation> citations;
                try {
                    is = new FileInputStream(file);
                    InputSource source = new InputSource(is);
                    citations = NlmCitationExtractor.extractCitations(source);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }

                citations = citations.subList(0, 500);

                for (Citation citation : citations) {
                    allcitations.add(citation);
                    for (CitationToken citationToken : citation.getTokens()) {
                        if (citationToken.getText().matches("^[a-zA-Z]+$")) {
                            String word = citationToken.getText().toLowerCase();
                            if (wordMap.get(word) == null) {
                                wordMap.put(word, 0);
                            }
                            wordMap.put(word, wordMap.get(word) + 1);
                        }
                    }
                }
            }

            List<Entry<String, Integer>> wordCounts = new ArrayList<Entry<String, Integer>>();
            for (Entry<String, Integer> entry : wordMap.entrySet()) {
                wordCounts.add(entry);
            }
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
                if (wordCount.getValue() > MIN_COUNT) {
                    additionalFeatures.add(wordCount.getKey());
                }
            }

            writer = new FileWriter(OUT_FILE);
            FileWriter writer2 = new FileWriter(OUT_FILE2);

            for (String s : additionalFeatures) {
                writer2.write(s);
                writer2.write("\n");
            }
            writer2.flush();
            writer2.close();
            System.out.println(allcitations.size());
            for (Citation citation : allcitations) {
                List<String> tokens = CitationUtils.citationToMalletInputFormat(citation);
                for (String token : tokens) {
                    writer.write(token);
                    writer.write("\n");
                }
                writer.write("\n");
            }

            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private MalletTrainingFileGenerator() {
    }
}
