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

package pl.edu.icm.cermine.evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.JDOMException;
import org.xml.sax.InputSource;
import pl.edu.icm.cermine.bibref.CRFBibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.bibref.parsing.tools.NlmCitationExtractor;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ReferenceParsingEvaluator {

    public static void main(String[] args) throws JDOMException, IOException, AnalysisException {
        if (args.length != 3) {
            System.err.println("USAGE: ReferenceParsingEvaluator <foldness> <model_path> <test_path>");
            System.exit(1);
        }
        
        int foldness = Integer.parseInt(args[0]);
        String modelPathSuffix = args[1];
        String testPathSuffix = args[2];

        Map<String, List<Result>> results = new HashMap<String, List<Result>>();
        
        for (int i = 0; i < foldness; i++) {
            System.out.println("Fold "+i);
            String modelPath = modelPathSuffix + i;
            CRFBibReferenceParser parser = new CRFBibReferenceParser(modelPath);
            
            String testPath = testPathSuffix + i;

            File testFile = new File(testPath);
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
            
            System.out.println(testCitations.size());
            
            List<BibEntry> testEntries = new ArrayList<BibEntry>();

            for (Citation c : testCitations) {
                BibEntry entry = CitationUtils.citationToBibref(c);
                testEntries.add(entry);
                for (String key : entry.getFieldKeys()) {
                    if (results.get(key) == null) {
                        results.put(key, new ArrayList<Result>());
                    }
                }
            }
            
            int j = 0;
            for (BibEntry orig : testEntries) {
                BibEntry test = parser.parseBibReference(orig.getText());

                System.out.println();
                System.out.println();
                System.out.println(orig.toBibTeX());
                System.out.println(test.toBibTeX());
            
                Map<String, Result> map = new HashMap<String, Result>();
                for (String s : orig.getFieldKeys()) {
                    if (map.get(s) == null) {
                        map.put(s, new Result());
                    }
                    map.get(s).addOrig(orig.getAllFieldValues(s).size());
                }
                for (String s : test.getFieldKeys()) {
                    if (map.get(s) == null) {
                        map.put(s, new Result());
                    }
                    map.get(s).addExtr(test.getAllFieldValues(s).size());
                }
                for (String s : test.getFieldKeys()) {
                    List<String> origVals = orig.getAllFieldValues(s);
                    for (String testVal : test.getAllFieldValues(s)) {
                        boolean found = false;
                        if (origVals.contains(testVal)) {
                            map.get(s).addSuccess();
                            origVals.remove(testVal);
                            found = true;
                        }
                        if (!found) {
                            System.out.println("WRONG "+s);
                        }
                    }
                }
                
                for (Map.Entry<String, Result> s : map.entrySet()) {
                    System.out.println("");
                    System.out.println(s.getKey());
                    System.out.println(s.getValue());
                    System.out.println(s.getValue().getPrecision());
                    System.out.println(s.getValue().getRecall());
                    results.get(s.getKey()).add(s.getValue());
                }
                
                j++;
                System.out.println("Tested "+j+" out of "+testEntries.size());
            }
            
        }
        
        for (Map.Entry<String, List<Result>> e : results.entrySet()) {
            System.out.println("");
            System.out.println(e.getKey());
            System.out.println(e.getValue().size());
            double precision = 0;
            int precisionCount = 0;
            double recall = 0;
            int recallCount = 0;
            for (Result r : e.getValue()) {
                if (r.getPrecision() != null) {
                    precision += r.getPrecision();
                    precisionCount++;
                }
                if (r.getRecall() != null) {
                    recall += r.getRecall();
                    recallCount++;
                }
            }
            System.out.println("Precision count "+precisionCount);
            System.out.println("Mean precision "+(precision / precisionCount));
            System.out.println("Recall count "+recallCount);
            System.out.println("Mean recall "+(recall / recallCount));
        }
    }
    
    private static class Result {

        private int totalOrig = 0;
        private int totalExtr = 0;
        private int success = 0;

        public void addSuccess() {
            success++;
        }

        public void addOrig(int totalorig) {
            this.totalOrig += totalorig;
        }

        public void addExtr(int totalextr) {
            this.totalExtr += totalextr;
        }

        public Double getPrecision() {
            return (totalExtr == 0) ? null : (double) success / totalExtr;
        }
        
        public Double getRecall() {
            return (totalOrig == 0) ? null : (double) success / totalOrig;
        }

        @Override
        public String toString() {
            return "Result{" + "totalOrig=" + totalOrig + ", totalExtr=" + totalExtr + ", success=" + success + '}';
        }
        
    }

}
