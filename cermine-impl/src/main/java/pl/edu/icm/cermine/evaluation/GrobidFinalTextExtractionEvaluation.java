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
import pl.edu.icm.cermine.evaluation.tools.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class GrobidFinalTextExtractionEvaluation {

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

        List<ComparisonResult> headers = new ArrayList<ComparisonResult>();
        
        if (mode == 1) {
            System.out.println("path,gro_header,one");
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

            
            // headers
            List<String> expAll = new ArrayList<String>();
            List<String> extrAll = new ArrayList<String>();
            
            List<Node> expNodes = XMLTools.extractNodes(originalNlm, "/article/body//sec/title");
            for (Node expNode : expNodes) {
                int k = 0;
                Node parent = expNode.getParentNode();
                while ("sec".equals(parent.getNodeName())) {
                    parent = parent.getParentNode();
                    k++;
                }
                if (k <= 3) {
                    String h = XMLTools.extractTextFromNode(expNode).trim().toLowerCase().replaceAll("[^a-zA-Z ]", "");
                    if (isProper(h)) {
                        expAll.add(h);
                    }
                }
            }

            List<Node> extNodes = XMLTools.extractNodes(extractedNlm, "/TEI/text/body/div/head");
            for (Node extNode : extNodes) {
                String h = XMLTools.extractTextFromNode(extNode).trim().toLowerCase().replaceAll("[^a-zA-Z ]", "");
                if (isProper(h)) {
                    extrAll.add(h);
                }
            }

            MetadataList header = new MetadataList(removeReferences(expAll), removeReferences(extrAll));
            header.setComp(EvaluationUtils.swComparator);
                        
            header.print(mode, "Headers");
            headers.add(header);
            
            
            if (mode == 1) {
                System.out.println("1");
            }
        }
      
        if (mode != 1) {
            System.out.println("==== Summary (" + iter.size() + " docs)====");

            PrecisionRecall headersPR = new PrecisionRecall().build(headers);
            headersPR.print("Headers");
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

        GrobidFinalTextExtractionEvaluation e = new GrobidFinalTextExtractionEvaluation();
        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        e.evaluate(mode, iter);
    }

}
