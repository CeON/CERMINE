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
import pl.edu.icm.cermine.bibref.CRFBibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.bibref.parsing.tools.NlmCitationExtractor;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class ReferenceParsingEvaluator {

    public static void main(String[] args) throws JDOMException, IOException, AnalysisException {
        if (args.length != 1) {
            System.err.println("USAGE: ReferenceParsingEvaluator <test file>");
            System.exit(1);
        }

        File testFile = new File(args[0]);

        List<Citation> testCitations;
        
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

        List<BibEntry> testEntries = new ArrayList<BibEntry>();

        Map<String, Result> results = new HashMap<String, Result>();

        for (Citation c : testCitations) {
            BibEntry entry = CitationUtils.citationToBibref(c);
            testEntries.add(entry);
            for (String key : entry.getFieldKeys()) {
                if (results.get(key) == null) {
                    results.put(key, new Result());
                }
            }
        }

        int i = 0;
        for (BibEntry orig : testEntries) {

            CRFBibReferenceParser parser = CRFBibReferenceParser.getInstance();
            BibEntry test = parser.parseBibReference(orig.getText());

            System.out.println();
            System.out.println();
            System.out.println(orig.toBibTeX());
            System.out.println(test.toBibTeX());
            
            for (String s : orig.getFieldKeys()) {
                results.get(s).addTotalOrig(orig.getAllFieldValues(s).size());
            }
            for (String s : test.getFieldKeys()) {
                results.get(s).addTotalExtr(test.getAllFieldValues(s).size());
                List<String> origVals = orig.getAllFieldValues(s);
                for (String testVal : test.getAllFieldValues(s)) {
                    boolean found = false;
                    if (origVals.contains(testVal)) {
                        results.get(s).addSuccess();
                        origVals.remove(testVal);
                        found = true;
                    }
                    if (!found) {
                        System.out.println("WRONG "+s);
                    }
                }
            }
            i++;
            System.out.println("Tested "+i+" out of "+testEntries.size());
        }

        for (Entry<String, Result> entry : results.entrySet()) {
            System.out.println(entry.getKey() + ": ");
            System.out.println("    "+entry.getValue());
            System.out.println("    Precission = " + ((double) entry.getValue().success * 100.0 / (double) entry.getValue().totalExtr));
            System.out.println("    Recall = " + ((double) entry.getValue().success * 100.0 / (double) entry.getValue().totalOrig));
            System.out.println("");
        }
    }

    private static class Result {

        private int totalOrig = 0;
        private int totalExtr = 0;
        private int success = 0;

        public void addSuccess() {
            success++;
        }

        public void addTotalOrig(int totalorig) {
            this.totalOrig += totalorig;
        }

        public void addTotalExtr(int totalextr) {
            this.totalExtr += totalextr;
        }

        @Override
        public String toString() {
            return "Result{" + "totalOrig=" + totalOrig + ", totalExtr=" + totalExtr + ", success=" + success + '}';
        }
        
    }

}
