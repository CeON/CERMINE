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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.evaluation.tools.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class FinalReferenceExtractionEvaluation {

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
        
        public PrecisionRecall buildForList(List<MetadataList> metadataList) {
            int precisions = 0;
            int recalls = 0;
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
                    100*calculatePrecision(), 100*calculateRecall(), 100*calculateF1());
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

    public void evaluate(NlmIterator iter) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, XPathExpressionException, TransformerException {

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
        
        List<MetadataList> references = new ArrayList<MetadataList>();
        PrecisionRecall total = new PrecisionRecall();
        
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

            List<Node> originalRefNodes = XMLTools.extractNodes(originalNlm, "//ref-list/ref");
            List<Node> extractedRefNodes = XMLTools.extractNodes(extractedNlm, "//ref-list/ref");//cermxml, pdfx
//            List<Node> extractedRefNodes = XMLTools.extractNodes(extractedNlm, "//listBibl/biblStruct");//tei
//            List<Node> extractedRefNodes = XMLTools.extractNodes(extractedNlm, "//citationList/citation/rawString");//parscit
    
            List<String> originalRefs = new ArrayList<String>();
            List<String> extractedRefs = new ArrayList<String>();
            
            for (Node originalRefNode : originalRefNodes) {
                originalRefs.add(XMLTools.extractTextFromNode(originalRefNode).trim());
            }
            for (Node extractedRefNode : extractedRefNodes) {
                extractedRefs.add(XMLTools.extractTextFromNode(extractedRefNode).trim());
            }
            
            MetadataList refs = new MetadataList(originalRefs, extractedRefs);
            references.add(refs);
            refs.print("references");
            
            total.correct += refs.correctCount;
            total.expected += refs.expectedCount;
            total.extracted += refs.extractedCount;

        }
      
        System.out.println("==== Summary (" + iter.size() + " docs)====");

        total.print("Total");
        PrecisionRecall refsPR = new PrecisionRecall().buildForList(references);
        refsPR.print("Mean on docs");
    }

    public static void main(String[] args) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {
        if (args.length != 3) {
            System.out.println("Usage: FinalMetadataExtractionEvaluation <input dir> <orig extension> <extract extension>");
            return;
        }
        String directory = args[0];
        String origExt = args[1];
        String extrExt = args[2];

        FinalReferenceExtractionEvaluation e = new FinalReferenceExtractionEvaluation();
        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        e.evaluate(iter);
    }

    static org.w3c.dom.Document elementToW3CDocument(org.jdom.Element elem) throws JDOMException {
        org.jdom.Document metaDoc = new org.jdom.Document();
        metaDoc.setRootElement(elem);
        org.jdom.output.DOMOutputter domOutputter = new DOMOutputter();
        return domOutputter.output(metaDoc);
    }

    static String outputDoc(Document document) throws IOException, TransformerException {
        OutputFormat format = new OutputFormat(document);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(document);
        return out.toString();
    }
    
    private static class MetadataList {
        private List<String> expectedValue;
        private List<String> extractedValue;
        private Double precision;
        private Double recall;
        private int expectedCount;
        private int extractedCount;
        private int correctCount;

        public MetadataList(List<String> expectedValue, List<String> extractedValue) {
            this.expectedValue = expectedValue;
            this.extractedValue = extractedValue;
            this.expectedCount = expectedValue.size();
            this.extractedCount = extractedValue.size();
            
            int correct = 0;
            CosineDistance cos = new CosineDistance();
        
            List<String> tmp = new ArrayList<String>(expectedValue);
            external:
            for (String partExt : extractedValue) {
                for (String partExp : tmp) {
                    if (cos.compare(StringTools.tokenize(partExt), StringTools.tokenize(partExp))+0.001 > .6/*Math.sqrt(2) / 2*/) {
                        ++correct;
                        tmp.remove(partExp);
                        continue external;
                    }
                }
            }
            
            this.correctCount = correct;
            
            if (extractedCount == 0) {
                this.precision = null;
            } else {
                this.precision = (double) correct / extractedCount;
            }
            if (expectedCount == 0) {
                this.recall = null;
            } else {
                this.recall = (double) correct / expectedCount;
            }
        }

        public boolean hasExpected() {
            return expectedValue != null && !expectedValue.isEmpty();
        }
        
        public boolean hasExtracted() {
            return extractedValue != null && !extractedValue.isEmpty();
        }

        public Double getPrecision() {
            return precision;
        }

        public Double getRecall() {
            return recall;
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
    
}
    