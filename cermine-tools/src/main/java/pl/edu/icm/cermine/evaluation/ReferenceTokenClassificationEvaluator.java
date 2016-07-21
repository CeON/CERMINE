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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.JDOMException;
import org.xml.sax.InputSource;
import pl.edu.icm.cermine.bibref.CRFBibReferenceParser;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.tools.NlmCitationExtractor;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ReferenceTokenClassificationEvaluator {

    public static void main(String[] args) throws JDOMException, IOException, AnalysisException {
        if (args.length != 3) {
            System.err.println("USAGE: ReferenceParsingEvaluator <foldness> <model_path> <test_path>");
            System.exit(1);
        }
        
        int foldness = Integer.parseInt(args[0]);
        String modelPathSuffix = args[1];
        String testPathSuffix = args[2];

        Map<String, Map<String, Integer>> origToTest = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> testToOrig = new HashMap<String, Map<String, Integer>>();
        
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
            
            for (Citation orig : testCitations) {
                Citation test = parser.parseToTokenList(orig.getText());
            
                assert(test.getTokens().size() == orig.getTokens().size());
                
                for (int j = 0; j < test.getTokens().size(); j++) {
                    String origLabel = orig.getTokens().get(j).getLabel().name();
                    String testLabel = test.getTokens().get(j).getLabel().name();
                    
                    if (origToTest.get(origLabel) == null) {
                        origToTest.put(origLabel, new HashMap<String, Integer>());
                    }
                    if (origToTest.get(origLabel).get(testLabel) == null) {
                        origToTest.get(origLabel).put(testLabel, 0);
                    }
                    origToTest.get(origLabel).put(testLabel, origToTest.get(origLabel).get(testLabel)+1);
                    
                    if (testToOrig.get(testLabel) == null) {
                        testToOrig.put(testLabel, new HashMap<String, Integer>());
                    }
                    if (testToOrig.get(testLabel).get(origLabel) == null) {
                        testToOrig.get(testLabel).put(origLabel, 0);
                    }
                    testToOrig.get(testLabel).put(origLabel, testToOrig.get(testLabel).get(origLabel)+1);
                   
                }
            }
            
        }
       
        String[] labels = new String[]{"GIVENNAME", "SURNAME", "ARTICLE_TITLE",
                                       "SOURCE", "VOLUME", "ISSUE", "YEAR", "PAGEF",
                                       "PAGEL", "TEXT"};
        double meanPrecision = 0;
        double meanRecall = 0;
        double meanFScore = 0;
        for (String orig : labels) {
            System.out.println("Original "+orig);
            System.out.println(origToTest.get(orig));
            for (String test : labels) {
                if (origToTest.get(orig).get(test) == null) {
                    System.out.print("0 ");
                } else {
                    System.out.print(origToTest.get(orig).get(test) + " ");
                }
            }
            System.out.println("");
            int total = 0;
            for (int val : testToOrig.get(orig).values()) {
                total += val;
            }
            double precision = (double)origToTest.get(orig).get(orig) / total;
            System.out.println("Precision: " + precision);
            meanPrecision += precision;
            total = 0;
            for (int val : origToTest.get(orig).values()) {
                total += val;
            }
            double recall = (double)origToTest.get(orig).get(orig) / total;
            System.out.println("Recall: " + recall);
            meanRecall += recall;
            double fScore = 2*precision*recall/(precision+recall);
            System.out.println("F-Score: " + fScore);
            meanFScore += fScore;
            System.out.println("");
        }

        meanPrecision /= labels.length;
        meanRecall /= labels.length;
        meanFScore /= labels.length;
        
        System.out.println("Mean precision " + meanPrecision);
        System.out.println("Mean recall " + meanRecall);
        System.out.println("Mean f-score " + meanFScore);
    }
    
}
