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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.evaluation.tools.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class FinalTextExtractionEvaluation {

    private static class PrecisionRecall {

        public int correct;
        public int expected;
        public int extracted;
        
        public double precision;
        public double recall;
        
        public PrecisionRecall() {
            correct = 0;
            expected = 0;
            extracted = 0;
            precision = -1.;
            recall = -1.;
        }

        public PrecisionRecall buildForRelation(List<MetadataRelation> metadataList) {
            int precisions = 0;
            int recalls = 0;
            precision = 0;
            recall = 0;
            for (MetadataRelation metadata : metadataList) {
                if (metadata.getPrecision() != null) {
                    precision += metadata.getPrecision();
                    precisions++;
                }
                if (metadata.getRecall() != null) {
                    recall += metadata.getRecall();
                    recalls++;
                }
            }
            precision /= precisions;
            recall /= recalls;
            return this;
        }

        public PrecisionRecall buildForList(List<MetadataList> metadataList) {
            int precisions = 0;
            int recalls = 0;
            precision = 0;
            recall = 0;
            
            for (MetadataList metadata : metadataList) {
                if (metadata.getPrecision() != null) {
                    precision += metadata.getPrecision();
                    precisions++;
                }
                if (metadata.getRecall() != null) {
                    recall += metadata.getRecall();
                    recalls++;
                }
            }
            precision /= precisions;
            recall /= recalls;

            return this;
        }
        
        @Override
        public String toString() {
            return "PrecisionRecall{" + "correct=" + correct + ", expected=" + expected + ", extracted=" + extracted + '}';
        }
        
        public void print(String name) {
            System.out.println(name + ": " + toString());
            System.out.printf(name + ": precision: %4.2f" + ", recall: %4.2f" + ", F1: %4.2f\n\n",
                    calculatePrecision() == null ? -1. : 100 * calculatePrecision(), 
                    calculateRecall() == null ? -1. : 100 * calculateRecall(), 
                    calculateF1() == null ? -1. : 100 * calculateF1());
        }

        public Double calculateRecall() {
            if (recall != -1.) {
                return recall;
            }
            if (expected == 0) {
                return null;
            }
            return (double) correct / expected;
        }
        
        public Double calculatePrecision() {
            if (precision != -1.) {
                return precision;
            }
            if (extracted == 0) {
                return null;
            }
            return (double) correct / extracted;
        }
        
        public Double calculateF1() {
            Double prec = calculatePrecision();
            Double rec = calculateRecall();
            if (prec == null || rec == null) {
                return null;
            }
            return 2 * prec * rec / (prec + rec);
        }
    }

    public void evaluate(NlmIterator iter) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {

        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        javax.xml.parsers.DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

        SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        builder.setValidation(false);
        builder.setFeature("http://xml.org/sax/features/validation", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        List<MetadataRelation> headersLevels = new ArrayList<MetadataRelation>();
        List<MetadataList> headers = new ArrayList<MetadataList>();
        
        int i = 0;
        for (NlmPair pair : iter) {
            i++;
            System.out.println("");
            System.out.println(">>>>>>>>> "+i);
            
            System.out.println(pair.getExtractedNlm().getPath());

            org.w3c.dom.Document originalNlm;
            org.w3c.dom.Document extractedNlm;
            try {
                originalNlm = documentBuilder.parse(new FileInputStream(pair.getOriginalNlm()));
                extractedNlm = documentBuilder.parse(new FileInputStream(pair.getExtractedNlm()));
            } catch (SAXException ex) {
                i--;
                continue;
            }
            
            MetadataRelation headersLevel = new MetadataRelation();
            
            List<String> expTitles = XMLTools.extractTextAsList(originalNlm, "/article/body/sec/title");
            List<String> extrTitles = XMLTools.extractTextAsList(extractedNlm, "/article/body/sec/title");
            if (!extrTitles.isEmpty() && "-".equals(extrTitles.get(0))) {
                extrTitles.remove(0);
            }
            
            List<String> expAll = new ArrayList<String>();
            List<String> extrAll = new ArrayList<String>();
            expAll.addAll(expTitles);
            extrAll.addAll(extrTitles);
            
            for (String e : expTitles) {
                headersLevel.addExpected(new StringRelation("1", e));
            }
            for (String e : extrTitles) {
                headersLevel.addExtracted(new StringRelation("1", e));
            }

            expTitles = XMLTools.extractTextAsList(originalNlm, "/article/body/sec/sec/title");
            extrTitles = XMLTools.extractTextAsList(extractedNlm, "/article/body/sec/sec/title");
            
            expAll.addAll(expTitles);
            extrAll.addAll(extrTitles);
            
            for (String e : expTitles) {
                headersLevel.addExpected(new StringRelation("2", e));
            }
            for (String e : extrTitles) {
                headersLevel.addExtracted(new StringRelation("2", e));
            }
            
            expTitles = XMLTools.extractTextAsList(originalNlm, "/article/body/sec/sec/sec/title");
            extrTitles = XMLTools.extractTextAsList(extractedNlm, "/article/body/sec/sec/sec/title");
            
            expAll.addAll(expTitles);
            extrAll.addAll(extrTitles);
            
            for (String e : expTitles) {
                headersLevel.addExpected(new StringRelation("3", e));
            }
            for (String e : extrTitles) {
                headersLevel.addExtracted(new StringRelation("3", e));
            }
            
            headersLevel.print("Headers levels");
            headersLevels.add(headersLevel);
            
            MetadataList header = new MetadataList(expAll, extrAll);
            
            header.print("Headers");
            
            headers.add(header);
        }
      
        System.out.println("==== Summary (" + iter.size() + " docs)====");

        PrecisionRecall headersLevelsPR = new PrecisionRecall().buildForRelation(headersLevels);
        headersLevelsPR.print("Level - header");
        
        PrecisionRecall headersPR = new PrecisionRecall().buildForList(headers);
        headersPR.print("Headers");
    }

    public static void main(String[] args) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {
        if (args.length != 3) {
            System.out.println("Usage: FinalMetadataExtractionEvaluation <input dir> <orig extension> <extract extension>");
            return;
        }
        String directory = args[0];
        String origExt = args[1];
        String extrExt = args[2];

        FinalTextExtractionEvaluation e = new FinalTextExtractionEvaluation();
        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        e.evaluate(iter);
    }

    private static double compareStringsSW(String expectedText, String extractedText) {
        List<String> expectedTokens = StringTools.tokenize(expectedText.trim());
        List<String> extractedTokens = StringTools.tokenize(extractedText.trim());
        SmithWatermanDistance distanceFunc = new SmithWatermanDistance(.0, .0);
        double distance = distanceFunc.compare(expectedTokens, extractedTokens);
        return 2*distance / (double) (expectedTokens.size()+extractedTokens.size());
    }

    private static double calculatePrecision(List<String> expected, List<String> extracted) {
        if (extracted.isEmpty()) {
            return .0;
        }
        int correct = 0;
       
        List<String> tmp = new ArrayList<String>(expected);
        external:
        for (String partExt : extracted) {
            for (String partExp : tmp) {
                if (compareStringsSW(partExt, partExp) >= 0.9) {
                    ++correct;
                    tmp.remove(partExp);
                    continue external;
                }
            }
        }
        return (double) correct / extracted.size();
    }

    private static double calculateRecall(List<String> expected, List<String> extracted) {
        int correct = 0;

        List<String> tmp = new ArrayList<String>(expected);
        external:
        for (String partExt : extracted) {
            internal:
            for (String partExp : tmp) {
                if (compareStringsSW(partExt, partExp) >= 0.9) {
                    ++correct;
                    tmp.remove(partExp);
                    continue external;
                }
            }
        }
        return (double) correct / expected.size();
    }
    
    private static class MetadataList {
        private List<String> expectedValue;
        private List<String> extractedValue;
        private Double precision = -1.;
        private Double recall = -1.;

        public MetadataList(List<String> expectedValue, List<String> extractedValue) {
            this.expectedValue = expectedValue;
            this.extractedValue = extractedValue;
            this.precision = -1.;
            this.recall = -1.;
        }

        public boolean hasExpected() {
            return expectedValue != null && !expectedValue.isEmpty();
        }
        
        public boolean hasExtracted() {
            return extractedValue != null && !extractedValue.isEmpty();
        }
        
        public Double getPrecision() {
            if (precision != -1.) {
                return precision;
            }
            if (!hasExtracted()) {
                return null;
            }
            return calculatePrecision(expectedValue, extractedValue);
        }
        
        public Double getRecall() {
            if (recall != -1.) {
                return recall;
            }
            if (!hasExpected()) {
                return null;
            }
            return calculateRecall(expectedValue, extractedValue);
        }
        
        public void print(String name) {
            System.out.println("");
            System.out.println("Expected " + name + ":");
            for (String expected : expectedValue) {
                System.out.println("    "+expected);
            }
            System.out.println("Extracted " + name + ":");
            for (String extracted : extractedValue) {
                System.out.println("    "+extracted);
            }
            System.out.printf("Precision: %4.2f\n", getPrecision());
            System.out.printf("Recall: %4.2f\n", getRecall());
        }
        
    }
    
    private static class MetadataRelation {
        private Set<StringRelation> expectedValue = new HashSet<StringRelation>();
        private Set<StringRelation> extractedValue = new HashSet<StringRelation>();

        public void addExpected(StringRelation relation) {
            expectedValue.add(relation);
        }
        
        public void addExtracted(StringRelation relation) {
            extractedValue.add(relation);
        }
        
        public boolean hasExpected() {
            return expectedValue != null && !expectedValue.isEmpty();
        }
        
        public boolean hasExtracted() {
            return extractedValue != null && !extractedValue.isEmpty();
        }
        
        public Double getPrecision() {
            if (!hasExtracted()) {
                return null;
            }
            int correct = 0;
            
            List<StringRelation> tmp = new ArrayList<StringRelation>(expectedValue);
            external:
            for (StringRelation partExt : extractedValue) {
                for (StringRelation partExp : tmp) {
                    if (compareStringsSW(partExp.element2, partExt.element2) >= 0.9
                            && partExp.element1.equals(partExt.element1)) {
                        
                        ++correct;
                        tmp.remove(partExp);
                        continue external;
                    }
                }
            }
            return (double) correct / extractedValue.size();
        }
        
        public Double getRecall() {
            if (!hasExpected()) {
                return null;
            }
            int correct = 0;

            List<StringRelation> tmp = new ArrayList<StringRelation>(expectedValue);
            external:
            for (StringRelation partExt : extractedValue) {
                internal:
                for (StringRelation partExp : tmp) {
                    if (compareStringsSW(partExp.element2, partExt.element2) >= 0.9
                            && partExp.element1.equals(partExt.element1)) {
                        ++correct;
                        tmp.remove(partExp);
                        continue external;
                    }
                }
            }
            return (double) correct / expectedValue.size();
        }
        
        public void print(String name) {
            System.out.println("");
            System.out.println("Expected " + name + ":");
            for (StringRelation expected : expectedValue) {
                System.out.println("    "+expected);
            }
            System.out.println("Extracted " + name + ":");
            for (StringRelation extracted : extractedValue) {
                System.out.println("    "+extracted);
            }
            System.out.printf("Precision: %4.2f\n", getPrecision());
            System.out.printf("Recall: %4.2f\n", getRecall());
        }
        
    }

    private static class StringRelation {
        private String element1;
        private String element2;

        public StringRelation(String element1, String element2) {
            this.element1 = element1;
            this.element2 = element2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StringRelation other = (StringRelation) obj;
            if ((this.element1 == null) ? (other.element1 != null) : !this.element1.equals(other.element1)) {
                return false;
            }
            if ((this.element2 == null) ? (other.element2 != null) : !this.element2.equals(other.element2)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + (this.element1 != null ? this.element1.hashCode() : 0);
            hash = 67 * hash + (this.element2 != null ? this.element2.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return element1 + " --- " + element2;
        }
        
    }
    
}
