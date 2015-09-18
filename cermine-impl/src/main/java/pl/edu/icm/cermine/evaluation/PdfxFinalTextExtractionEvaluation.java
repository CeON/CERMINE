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

import com.google.common.collect.Lists;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.evaluation.tools.MetadataRelation.StringRelation;
import pl.edu.icm.cermine.evaluation.tools.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.XMLTools;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class PdfxFinalTextExtractionEvaluation {

    public void evaluate(int mode, NlmIterator iter) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {

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

        List<ComparisonResult> headersLevels = new ArrayList<ComparisonResult>();
        List<ComparisonResult> headers = new ArrayList<ComparisonResult>();
        List<ComparisonResult> headers0 = new ArrayList<ComparisonResult>();
        List<ComparisonResult> headers1 = new ArrayList<ComparisonResult>();
        List<ComparisonResult> headers2 = new ArrayList<ComparisonResult>();
        List<ComparisonResult> headers3 = new ArrayList<ComparisonResult>();
        
        if (mode == 1) {
            System.out.println("path,pdfx_header,pdfx_hlevel,pdfx_header0,"
                    + "pdfx_header1,pdfx_header2,pdfx_header3,one");
        }
        
        int i = 0;
        for (NlmPair pair : iter) {
            i++;
            if (mode == 0) {
                System.out.println("");
                System.out.println(">>>>>>>>> "+i);
                System.out.println(pair.getExtractedNlm().getPath());
            }
            if (mode == 1) {
                System.out.print(pair.getOriginalNlm().getPath()+",");
            }

            org.w3c.dom.Document originalNlm;
            org.w3c.dom.Document extractedNlm;
            try {
                originalNlm = documentBuilder.parse(new FileInputStream(pair.getOriginalNlm()));
                extractedNlm = documentBuilder.parse(new FileInputStream(pair.getExtractedNlm()));
            } catch (SAXException ex) {
                i--;
                continue;
            }

            
            StringBuilder sb = new StringBuilder();
            List<Node> expNodes = XMLTools.extractNodes(originalNlm, "/article/body//sec");
            for (Node expNode : expNodes) {
                String h = "-";
                if (!XMLTools.extractChildrenNodesFromNode(expNode, "title").isEmpty()) {
                    h = XMLTools.extractTextFromNode(XMLTools.extractChildrenNodesFromNode(expNode, "title").get(0));
                }
                h = h.toLowerCase().replaceAll("[^a-zA-Z ]", "").trim();
                if (!isProper(h)) {
                    continue;
                }
                Node parent = expNode.getParentNode();
                if (!"body".equals(parent.getNodeName()) && !"sec".equals(parent.getNodeName())) {
                    continue;
                }
                int k = 0;
                while ("sec".equals(parent.getNodeName())) {
                    parent = parent.getParentNode();
                    k++;
                }
                if (k < 3) {
                    while (k > 0) {
                        sb.append(" ");
                        k--;
                    }
                    sb.append(h);
                    sb.append("\n");
                }
            }
            String expTree = sb.toString().trim();
            
            sb = new StringBuilder();
            List<Node> extNodes = XMLTools.extractNodes(extractedNlm, "/pdfx/article/body//section");
            for (Node extNode : extNodes) {
                String h = "-";
                if (!XMLTools.extractChildrenNodesFromNode(extNode, "h1").isEmpty()) {
                    h = XMLTools.extractTextFromNode(XMLTools.extractChildrenNodesFromNode(extNode, "h1").get(0));
                } else if (!XMLTools.extractChildrenNodesFromNode(extNode, "h2").isEmpty()) {
                    h = XMLTools.extractTextFromNode(XMLTools.extractChildrenNodesFromNode(extNode, "h2").get(0));
                } else if (!XMLTools.extractChildrenNodesFromNode(extNode, "h3").isEmpty()) {
                    h = XMLTools.extractTextFromNode(XMLTools.extractChildrenNodesFromNode(extNode, "h3").get(0));
                }
                h = h.toLowerCase().replaceAll("[^a-zA-Z ]", "").trim();
                if (!isProper(h)) {
                    continue;
                }
                Node parent = extNode.getParentNode();
                while ("section".equals(parent.getNodeName())) {
                    parent = parent.getParentNode();
                    sb.append(" ");
                }
                sb.append(h);
                sb.append("\n");
            }
            String extTree = sb.toString().trim();

            
            // headers
            List<String> expAll = new ArrayList<String>();
            List<String> extrAll = new ArrayList<String>();
            
            for (String exp : expTree.split("\n")) {
                expAll.add(exp.trim());
            }
            for (String ext : extTree.split("\n")) {
                extrAll.add(ext.trim());
            }
                        
            MetadataList header = new MetadataList(removeReferences(expAll), removeReferences(extrAll));
            header.setComp(EvaluationUtils.swComparator);
                        
            header.print(mode, "Headers");
            headers.add(header);
            
            
            // headers + levels
            MetadataRelation headersLevel = new MetadataRelation();
            headersLevel.setComp2(EvaluationUtils.swComparator);

            for (String exp : expTree.split("\n")) {
                String level = "1";
                if (exp.startsWith("  ")) {
                    level = "3";
                } else if (exp.startsWith(" ")) {
                    level = "2";
                }
                headersLevel.addExpected(new StringRelation(level, exp.trim()));
            }
            for (String ext : extTree.split("\n")) {
                String level = "1";
                if (ext.startsWith("  ")) {
                    level = "3";
                } else if (ext.startsWith(" ")) {
                    level = "2";
                }
                headersLevel.addExtracted(new StringRelation(level, ext.trim()));
            }
            
            headersLevel.print(mode, "Headers levels");
            headersLevels.add(headersLevel);

                      
            // headers 0
            List<String> extHeaders = new ArrayList<String>();
            List<String> expHeaders = new ArrayList<String>();

            if (!expTree.isEmpty()) {
                expHeaders.add(expTree);
            }
            if (!extTree.isEmpty()) {
                extHeaders.add(extTree);
            }
            
            MetadataList header0 = new MetadataList(expHeaders, extHeaders);
            header0.setComp(EvaluationUtils.headerComparator(EvaluationUtils.swComparator));
            header0.print(mode, "Headers 0");
            headers0.add(header0);
            
            
            // headers 1
            extHeaders = new ArrayList<String>();
            expHeaders = new ArrayList<String>();

            sb = new StringBuilder();
            for (String exp : expTree.split("\n")) {
                if (!exp.startsWith(" ")) {
                    if (!sb.toString().isEmpty()) {
                        expHeaders.add(sb.toString().trim());
                    }
                    sb = new StringBuilder();
                }
                sb.append(exp);
                sb.append("\n");
            }
            if (!sb.toString().isEmpty()) {
                expHeaders.add(sb.toString().trim());
            }
            
            sb = new StringBuilder();
            for (String ext : extTree.split("\n")) {
                if (!ext.startsWith(" ")) {
                    if (!sb.toString().isEmpty()) {
                        extHeaders.add(sb.toString().trim());
                    }
                    sb = new StringBuilder();
                }
                sb.append(ext);
                sb.append("\n");
            }
            if (!sb.toString().isEmpty()) {
                extHeaders.add(sb.toString().trim());
            }
            
            MetadataList header1 = new MetadataList(expHeaders, extHeaders);
            header1.setComp(EvaluationUtils.headerComparator(EvaluationUtils.swComparator));
            header1.print(mode, "Headers 1");
            headers1.add(header1);
            
            
            // headers 2
            extHeaders = new ArrayList<String>();
            expHeaders = new ArrayList<String>();

            sb = new StringBuilder();
            for (String exp : expTree.split("\n")) {
                if (exp.startsWith("  ")) {
                    sb.append(exp);
                    sb.append("\n");
                } else if (exp.startsWith(" ")) {
                    if (!sb.toString().isEmpty()) {
                        expHeaders.add(sb.toString().trim());
                    }
                    sb = new StringBuilder();
                    sb.append(exp);
                    sb.append("\n");
                } else {
                    continue;
                }
            }
            if (!sb.toString().isEmpty()) {
                expHeaders.add(sb.toString().trim());
            }
            
            sb = new StringBuilder();
            for (String ext : extTree.split("\n")) {
                if (ext.startsWith("  ")) {
                    sb.append(ext);
                    sb.append("\n");
                } else if (ext.startsWith(" ")) {
                    if (!sb.toString().isEmpty()) {
                        extHeaders.add(sb.toString().trim());
                    }
                    sb = new StringBuilder();
                    sb.append(ext);
                    sb.append("\n");
                } else {
                    continue;
                }
            }
            if (!sb.toString().isEmpty()) {
                extHeaders.add(sb.toString().trim());
            }
            
            MetadataList header2 = new MetadataList(expHeaders, extHeaders);
            header2.setComp(EvaluationUtils.headerComparator(EvaluationUtils.swComparator));
            header2.print(mode, "Headers 2");
            headers2.add(header2);
            
            
            // headers 3
            extHeaders = new ArrayList<String>();
            expHeaders = new ArrayList<String>();

            for (String exp : expTree.split("\n")) {
                if (exp.startsWith("  ")) {
                    expHeaders.add(exp);
                }
            }

            for (String ext : extTree.split("\n")) {
                if (ext.startsWith("  ")) {
                    extHeaders.add(ext);
                }
            }
           
            MetadataList header3 = new MetadataList(expHeaders, extHeaders);
            header3.setComp(EvaluationUtils.headerComparator(EvaluationUtils.swComparator));
            header3.print(mode, "Headers 3");
            headers3.add(header3);
            
            if (mode == 1) {
                System.out.println("1");
            }
        }
      
        if (mode != 1) {
            System.out.println("==== Summary (" + iter.size() + " docs)====");

            PrecisionRecall headersLevelsPR = new PrecisionRecall().build(headersLevels);
            headersLevelsPR.print("Level - header");
        
            PrecisionRecall headersPR = new PrecisionRecall().build(headers);
            headersPR.print("Headers");
        
            PrecisionRecall headers0PR = new PrecisionRecall().build(headers0);
            headers0PR.print("Headers 0");
        
            PrecisionRecall headers1PR = new PrecisionRecall().build(headers1);
            headers1PR.print("Headers 1");
        
            PrecisionRecall headers2PR = new PrecisionRecall().build(headers2);
            headers2PR.print("Headers 2");
        
            PrecisionRecall headers3PR = new PrecisionRecall().build(headers3);
            headers3PR.print("Headers 3");
        }
    }

    private boolean isProper(String header) {
        List<String> toDelete = Lists.newArrayList(
                "references", "acknowledgements", "acknowledgments", 
                "conflicts of interest", "declaration of interest", "appendix",
                "conflict of interest statement", "conflict of interest", "funding",
                "authors contributions", "competing interests");
        return !toDelete.contains(header);
    }
    
    private List<String> removeReferences(List<String> list) {
        List<String> removed = Lists.newArrayList(list);
        for (String element : list) {
            if (!isProper(element)) {
                removed.remove(element);
            }
        }
        return removed;
    }
    
    public static void main(String[] args) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {
        if (args.length != 3 && args.length != 4) {
            System.out.println("Usage: FinalMetadataExtractionEvaluation <input dir> <orig extension> <extract extension>");
            return;
        }
        String directory = args[0];
        String origExt = args[1];
        String extrExt = args[2];
        int mode = 0;
        if (args.length == 4 && args[3].equals("csv")) {
            mode = 1;
        }
        if (args.length == 4 && args[3].equals("q")) {
            mode = 2;
        }

        PdfxFinalTextExtractionEvaluation e = new PdfxFinalTextExtractionEvaluation();
        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        e.evaluate(mode, iter);
    }

}
