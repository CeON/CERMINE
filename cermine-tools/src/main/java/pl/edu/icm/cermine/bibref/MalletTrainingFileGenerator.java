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
import pl.edu.icm.cermine.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.bibref.parsing.tools.NlmCitationExtractor;
import pl.edu.icm.cermine.tools.CountMap;
import pl.edu.icm.cermine.tools.PrefixTree;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class MalletTrainingFileGenerator {

    private static final int MIN_TERM_COUNT = 3;
    private static final int MIN_JOURNAL_COUNT = 2;
    private static final int MIN_SURNAME_COUNT = 2;
    private static final int MIN_INST_COUNT = 1;

    public static void main(String[] args) throws JDOMException, IOException {
        File inputFile = new File(args[0]);
        List<Citation> citations = new ArrayList<Citation>();
        CountMap<String> termCounts = new CountMap<String>();
        CountMap<String> journalCounts = new CountMap<String>();
        CountMap<String> surnameCounts = new CountMap<String>();
        CountMap<String> instCounts = new CountMap<String>();
       
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
                String term = citationToken.getText().toLowerCase(Locale.ENGLISH);
                termCounts.add(term);
            }
            for (CitationToken citationToken : citation.getConcatenatedTokens()) {
                String term = citationToken.getText().toLowerCase(Locale.ENGLISH);
                if (citationToken.getLabel() == CitationTokenLabel.SOURCE) {
                    journalCounts.add(term);
                }
                if (citationToken.getLabel() == CitationTokenLabel.SURNAME) {
                    surnameCounts.add(term);
                }
                if (citationToken.getLabel() == CitationTokenLabel.INSTITUTION) {
                    instCounts.add(term);
                }
            }
        }
        
        List<Entry<String, Integer>> wordCounts = termCounts.getSortedEntries(MIN_TERM_COUNT);
        Set<String> additionalFeatures = new HashSet<String>();
        for (Entry<String, Integer> wordCount : wordCounts) {
            additionalFeatures.add(wordCount.getKey());
        }

        List<Entry<String, Integer>> jCounts = journalCounts.getSortedEntries(MIN_JOURNAL_COUNT);
        Set<String> journals = new HashSet<String>();
        for (Entry<String, Integer> jCount : jCounts) {
            journals.add(jCount.getKey());
        }
        PrefixTree journalTree = new PrefixTree(PrefixTree.START_TERM);
        journalTree.build(journals);

        List<Entry<String, Integer>> sCounts = surnameCounts.getSortedEntries(MIN_SURNAME_COUNT);
        Set<String> surnames = new HashSet<String>();
        for (Entry<String, Integer> sCount : sCounts) {
            surnames.add(sCount.getKey());
        }
        PrefixTree surnameTree = new PrefixTree(PrefixTree.START_TERM);
        surnameTree.build(surnames);
        
        List<Entry<String, Integer>> iCounts = instCounts.getSortedEntries(MIN_INST_COUNT);
        Set<String> insts = new HashSet<String>();
        for (Entry<String, Integer> iCount : iCounts) {
            insts.add(iCount.getKey());
        }
        PrefixTree instTree = new PrefixTree(PrefixTree.START_TERM);
        instTree.build(insts);
        
        Writer featuresWriter = null;
        Writer termsWriter = null;
        Writer journalsWriter = null;
        Writer surnamesWriter = null;
        Writer instsWriter = null;
            
        try {
            featuresWriter = new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8");
            termsWriter = new OutputStreamWriter(new FileOutputStream(args[2]), "UTF-8");
            journalsWriter = new OutputStreamWriter(new FileOutputStream(args[3]), "UTF-8");
            surnamesWriter = new OutputStreamWriter(new FileOutputStream(args[4]), "UTF-8");
            instsWriter = new OutputStreamWriter(new FileOutputStream(args[5]), "UTF-8");

            for (String s : additionalFeatures) {
                termsWriter.write(s);
                termsWriter.write("\n");
            }
            termsWriter.flush();
            termsWriter.close();
            
            for (String s : journals) {
                journalsWriter.write(s);
                journalsWriter.write("\n");
            }
            journalsWriter.flush();
            journalsWriter.close();
            
            for (String s : surnames) {
                surnamesWriter.write(s);
                surnamesWriter.write("\n");
            }
            surnamesWriter.flush();
            surnamesWriter.close();
            
            for (String s : insts) {
                instsWriter.write(s);
                instsWriter.write("\n");
            }
            instsWriter.flush();
            instsWriter.close();
            
            for (Citation citation : citations) {
                List<String> tokens = CitationUtils.citationToMalletInputFormat(citation, additionalFeatures, journalTree, surnameTree, instTree);
                for (String token : tokens) {
                    featuresWriter.write(token);
                    featuresWriter.write("\n");
                }
                featuresWriter.write("\n");
            }

            featuresWriter.flush();
        } finally {
            if (featuresWriter != null) {
                featuresWriter.close();
            }
            if (termsWriter != null) {
                termsWriter.close();
            }
        }
    }

    private MalletTrainingFileGenerator() {
    }
}