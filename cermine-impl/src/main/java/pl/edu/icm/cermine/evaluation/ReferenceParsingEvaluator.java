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

package pl.edu.icm.cermine.evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jdom.JDOMException;
import org.xml.sax.InputSource;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.bibref.parsing.tools.NlmCitationExtractor;

/**
 *
 * @author Dominika Tkaczyk
 */
public class ReferenceParsingEvaluator {

    public static void main(String[] args) throws JDOMException, IOException {
        if (args.length != 2) {
            System.err.println("USAGE: ReferenceParsingEvaluator <orig file> <test file>");
            System.exit(1);
        }

        File origFile = new File(args[0]);
        File testFile = new File(args[1]);

        List<Citation> origCitations;
        List<Citation> testCitations;
        
        InputStream origIS = null;
        try {
            origIS = new FileInputStream(origFile);
            InputSource origSource = new InputSource(origIS);
            origCitations = NlmCitationExtractor.extractCitations(origSource);
        } finally {
            if (origIS != null) {
                origIS.close();
            }
        }

        InputStream testIS = null;
        try {
            testIS = new FileInputStream(testFile);
            InputSource testSource = new InputSource(testIS);
            testCitations = NlmCitationExtractor.extractCitations(testSource);
        } finally {
            if (testIS != null) {
                testIS.close();
            }
        }

        List<BibEntry> origEntries = new ArrayList<BibEntry>();
        List<BibEntry> testEntries = new ArrayList<BibEntry>();

        Map<String, Result> results = new HashMap<String, Result>();

        for (Citation c : origCitations) {
            BibEntry entry = CitationUtils.citationToBibref(c);
            origEntries.add(entry);
            for (String key : entry.getFieldKeys()) {
                if (results.get(key) == null) {
                    results.put(key, new Result());
                }
            }
        }

        for (Citation c : testCitations) {
            BibEntry entry = CitationUtils.citationToBibref(c);
            testEntries.add(entry);
            for (String key : entry.getFieldKeys()) {
                if (results.get(key) == null) {
                    results.put(key, new Result());
                }
            }
        }

        for (int i = 0; i < origEntries.size(); i++) {
            BibEntry orig = origEntries.get(i);
            BibEntry test = testEntries.get(i);

            for (String s : orig.getFieldKeys()) {
                results.get(s).addTotalOrig(orig.getAllFieldValues(s).size());
            }
            for (String s : test.getFieldKeys()) {
                results.get(s).addTotalExtr(test.getAllFieldValues(s).size());
                List<String> origVals = orig.getAllFieldValues(s);
                for (String testVal : test.getAllFieldValues(s)) {
                    if (origVals.contains(testVal)) {
                        results.get(s).addSuccess();
                        origVals.remove(testVal);
                    } else {
                        String matched = null;
                        for (String origVal : origVals) {
                            if (testVal.contains(origVal) && origVal.length() * 3 >= testVal.length() * 2) {
                                results.get(s).addSuperstring(1);
                                matched = origVal;
                                break;
                            }
                        }
                        if (matched != null) {
                            origVals.remove(matched);
                        } else {
                            matched = null;
                            for (String origVal : origVals) {
                                if (origVal.contains(testVal) && testVal.length() * 3 >= origVal.length() * 2) {
                                    results.get(s).addSubstring(1);
                                    matched = origVal;
                                    break;
                                }
                            }
                            if (matched != null) {
                                origVals.remove(matched);
                            }
                        }
                    }
                }
            }
        }

        for (Entry<String, Result> entry : results.entrySet()) {
            System.out.println(entry.getKey() + ": ");
            System.out.println("    Precission = " + ((double) entry.getValue().success * 100.0 / (double) entry.getValue().totalExtr));
            System.out.println("    Recall = " + ((double) entry.getValue().success * 100.0 / (double) entry.getValue().totalOrig));
            System.out.println("    Superstring recall = " + ((double) entry.getValue().superstring * 100.0 / (double) entry.getValue().totalOrig));
            System.out.println("    Substring recall = " + ((double) entry.getValue().substring * 100.0 / (double) entry.getValue().totalOrig));
            System.out.println("");
        }
    }

    private static class Result {

        private int totalOrig = 0;
        private int totalExtr = 0;
        private int success = 0;
        private int superstring = 0;
        private int substring = 0;

        public void addSuccess() {
            success++;
        }

        public void addTotalOrig(int totalorig) {
            this.totalOrig += totalorig;
        }

        public void addTotalExtr(int totalextr) {
            this.totalExtr += totalextr;
        }

        public void addSubstring(int substring) {
            this.substring += substring;
        }

        public void addSuperstring(int superstring) {
            this.superstring += superstring;
        }
    }
}
