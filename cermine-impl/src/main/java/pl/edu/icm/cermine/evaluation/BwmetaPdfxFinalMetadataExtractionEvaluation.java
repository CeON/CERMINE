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
public final class BwmetaPdfxFinalMetadataExtractionEvaluation {

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

        List<ComparisonResult> titles = new ArrayList<ComparisonResult>();
        List<ComparisonResult> emails = new ArrayList<ComparisonResult>();
        List<ComparisonResult> abstracts = new ArrayList<ComparisonResult>();
        List<ComparisonResult> dois = new ArrayList<ComparisonResult>();
        List<ComparisonResult> references = new ArrayList<ComparisonResult>();

        if (mode == 1) {
            System.out.println("path,pdfx_title,pdfx_abstract,pdfx_email,"+
                "pdfx_doi,pdfx_refs,one");
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
            
            // Document's title
            MetadataSingle title = new MetadataSingle(originalNlm, "/bwmeta/element/name[not(@type)]",
                                                        extractedNlm, "//article-title[@class='DoCO:Title']");
            title.setComp(EvaluationUtils.swComparator);
            titles.add(title);
            title.print(mode, "title");

            
            // Abstract
            MetadataSingle abstrakt = new MetadataSingle(originalNlm, "/bwmeta/element/description[@type='abstract']",
                                                        extractedNlm, "//abstract[@class='DoCO:Abstract']");
            abstrakt.setComp(EvaluationUtils.swComparator);
            abstracts.add(abstrakt);
            abstrakt.print(mode, "abstract");
            
            
            // Email addresses
            MetadataList email = new MetadataList(originalNlm, "/bwmeta/element/contributor[@role='author']/attribute[@key='contact-email']/value",
                                                    extractedNlm, "//email");
            email.setComp(EvaluationUtils.emailComparator);
            emails.add(email);
            email.print(mode, "email");
            
            
            // DOI
            MetadataSingle doi = new MetadataSingle(originalNlm, "/bwmeta/element/id[@scheme='bwmeta1.id-class.DOI']/@value",
                                                        extractedNlm, "/pdfx/meta/doi");
            dois.add(doi);
            doi.print(mode, "DOI");
            
            
            //references
            List<Node> originalRefNodes = XMLTools.extractNodes(originalNlm, "//relation[@type='reference-to']/attribute[@key='reference-text']/value");
            List<Node> extractedRefNodes = XMLTools.extractNodes(extractedNlm, "//ref-list/ref");
        
            List<String> originalRefs = new ArrayList<String>();
            List<String> extractedRefs = new ArrayList<String>();
            for (Node originalRefNode : originalRefNodes) {
                originalRefs.add(XMLTools.extractTextFromNode(originalRefNode).trim());
            }
            for (Node extractedRefNode : extractedRefNodes) {
                extractedRefs.add(XMLTools.extractTextFromNode(extractedRefNode).trim());
            }
            
            MetadataList refs = new MetadataList(originalRefs, extractedRefs);
            refs.setComp(EvaluationUtils.cosineComparator(0.6));
            
            references.add(refs);
            refs.print(mode, "references");
            
            if (mode == 1) {
                System.out.println("1");
            }
        }

        if (mode != 1) {
            System.out.println("==== Summary (" + iter.size() + " docs)====");
        
            PrecisionRecall titlePR = new PrecisionRecall().build(titles);
            titlePR.print("Title");

            PrecisionRecall emailsPR = new PrecisionRecall().build(emails);
            emailsPR.print("Emails");
      
            PrecisionRecall abstractPR = new PrecisionRecall().build(abstracts);
            abstractPR.print("Abstract");

            PrecisionRecall doiPR = new PrecisionRecall().build(dois);
            doiPR.print("DOI");
        
            PrecisionRecall refsPR = new PrecisionRecall().build(references);
            refsPR.print("References");
        
            List<PrecisionRecall> results = Lists.newArrayList(
                titlePR, emailsPR, abstractPR, doiPR);
        
            double avgPrecision = 0;
            double avgRecall = 0;
            double avgF1 = 0;
            for (PrecisionRecall result : results) {
                avgPrecision += result.getPrecision();
                avgRecall += result.getRecall();
                avgF1 += result.getF1();
            }
            avgPrecision /= results.size();
            avgRecall /= results.size();
            avgF1 /= results.size();
  
            System.out.printf("Average precision\t\t%4.2f\n", 100 * avgPrecision);
            System.out.printf("Average recall\t\t%4.2f\n", 100 * avgRecall);
            System.out.printf("Average F1 score\t\t%4.2f\n", 100 * avgF1);
        }
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

        BwmetaPdfxFinalMetadataExtractionEvaluation e = new BwmetaPdfxFinalMetadataExtractionEvaluation();
        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        e.evaluate(mode, iter);
    }

}
